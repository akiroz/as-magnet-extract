defmodule ASMagnetExtract.MixProject do
  use Mix.Project
  def project, do: [
    app: :as_magnet_extract,
    version: "0.4.0",
    elixir: "~> 1.6",
    escript: [
      main_module: ASMagnetExtract,
    ],
    deps: [
      {:yaml_elixir, "~> 2.0"},
      {:httpoison, "~> 1.1"},
      {:floki, "~> 0.20"},
      {:honeydew, "~> 1.1.2"}
    ],
  ]
end
