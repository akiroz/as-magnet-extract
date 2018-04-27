defmodule ASMagnetExtract do
  alias Honeydew.Job

  def api_host, do: "http://www.anime-sharing.com"

  def main(args) do
    {:ok, _} = init_workers()
    search_id = List.first(args) || search("search.yml")
    IO.puts(:stderr, "Search ID: #{search_id}")
    {:get_page, [1, search_id]} |> Honeydew.async(:http, reply: true)
    loop(%{:search_id => search_id})
  end

  def init_workers do
    cores = System.schedulers_online()
    children = [
      Honeydew.queue_spec(:http, [
        failure_mode: {Honeydew.FailureMode.Retry, [times: 3]},
      ]),
      Honeydew.worker_spec(:http, ASMagnetExtract.HTTP, num: 24),
      Honeydew.queue_spec(:parser),
      Honeydew.worker_spec(:parser, ASMagnetExtract.Parser, num: cores),
    ]
    Supervisor.start_link(children, strategy: :one_for_one)
  end

  def search(param_file) do
    IO.puts(:stderr, "Search params: #{param_file}")
    {:ok, params} = YamlElixir.read_from_file(param_file)
    {:ok, location} = {:post_search, [params]}
                      |> Honeydew.async(:http, reply: true)
                      |> Honeydew.yield
    [search_id | _] = Regex.run(~r/[0-9]+$/, location)
    search_id
  end

  def extract_text_int(html, selector) do
    Floki.find(html, selector)
    |> List.first
    |> Floki.text
    |> String.trim
    |> (&Regex.run(~r/[0-9]+$/, &1)).()
    |> List.first
    |> Integer.parse
    |> elem(0)
  end

  def parse_result(body) do
    html = Floki.parse(body)
    pages = case Floki.find(html, "#pagination_top") do
      [] -> 1
      [pagination | _] -> extract_text_int(pagination, "a")
    end
    threads = extract_text_int(html, "#postpagestats")
    {pages, threads}
  end

  def loop(state) do
    case state do
      %{
        :thread_total => n,
        :thread_parsed => n
      } ->
        IO.puts(:stderr, "Done! (#{n} titles)")
        System.halt()

      _ -> nil
    end
    receive do
      %Job{task: {:get_page, [1, search_id]}, result: {:ok, body}} ->
        {:parse_page, [body]} |> Honeydew.async(:parser, reply: true)
        {pages, threads} = parse_result(body)
        if pages > 1 do
          for p <- 2..pages do
            {:get_page, [p, search_id]} |> Honeydew.async(:http, reply: true)
          end
        end
        Map.merge(state, %{:page_total => pages, :thread_total => threads})
        |> Map.update(:page_fetched, 1, &(&1 + 1))
        |> loop

      %Job{task: {:get_page, _}, result: {:ok, body}} ->
        {:parse_page, [body]} |> Honeydew.async(:parser, reply: true)
        loop(Map.update(state, :page_fetched, 1, &(&1 + 1)))

      %Job{task: {:parse_page, _}, result: {:ok, threads}} ->
        for path <- threads do
          {:get_thread, [path]} |> Honeydew.async(:http, reply: true)
        end
        loop(Map.update(state, :page_parsed, 1, &(&1 + 1)))

      %Job{task: {:get_thread, _}, result: {:ok, body}} ->
        {:parse_thread, [body]} |> Honeydew.async(:parser, reply: true)
        loop(Map.update(state, :thread_fetched, 1, &(&1 + 1)))

      %Job{task: {:parse_thread, _}, result: {:ok, {title, links}}} ->
        IO.puts(:stderr, title)
        for l <- links do
          IO.puts(l)
        end
        loop(Map.update(state, :thread_parsed, 1, &(&1 + 1)))

      _ ->
        loop(state)
    end
  end
end

defmodule ASMagnetExtract.HTTP do
  @behaviour Honeydew.Worker

  def get_page(page, search_id) do
    HTTPoison.get!(
      ASMagnetExtract.api_host()
      |> URI.merge("forum/search.php")
      |> URI.merge("?searchid=#{search_id}&page=#{page}"),
      [], hackney: [pool: :default]
    )
    |> Map.fetch!(:body)
  end

  def get_thread(path) do
    HTTPoison.get!(
      ASMagnetExtract.api_host()
      |> URI.merge("forum/#{path}"),
      [], hackney: [pool: :default]
    )
    |> Map.fetch!(:body)
  end

  def post_search(params) do
    HTTPoison.post!(
      ASMagnetExtract.api_host()
      |> URI.merge("forum/search.php")
      |> URI.merge("?do=process"),
      {:form, Enum.into(params, [])}, [], [
        follow_redirect: false,
        hackney: [pool: :default]
      ]
    )
    |> Map.fetch!(:headers)
    |> Enum.into(%{})
    |> Map.fetch!("Location")
  end
end

defmodule ASMagnetExtract.Parser do
  @behaviour Honeydew.Worker

  def parse_page(body) do
    Floki.parse(body)
    |> Floki.find("#searchbits")
    |> List.first
    |> Floki.find(".title")
    |> Enum.map(fn(title) ->
      Floki.attribute(title, "href") |> List.first
    end)
  end

  def parse_thread(body) do
    html = Floki.parse(body)
    title = Floki.find(html, ".threadtitle")
            |> List.first
            |> Floki.text
    links = Floki.find(html, ".postcontent")
            |> List.first
            |> Floki.find(".message")
            |> Enum.map(fn(msg) ->
              Regex.named_captures(~r/magnet:\?(?<q>.+)/, Floki.text(msg, sep: " "))
            end)
            |> Enum.filter(& !is_nil(&1))
            |> Enum.map(fn(%{"q" => q}) ->
              "magnet:?xt=#{URI.decode_query(q) |> Map.fetch!("xt")}"
            end)
    {title, links}
  end
end

