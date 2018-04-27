## as-magnet-extract

Simple script for grabbing magnet URIs from AS Forums

### Changes

0.4.1:
- Print number of threads and pages

0.4.0:
- Reimplement in Elixir
- Performance boost
  - Utilize keep-alive connections
  - Improved parallelism
- Network concurrency: 24
- `search.edn` -> `search.yml`

0.3.2:
- Fix page count when there's only 1 page

0.3.1:
- Correct spelling of `magnet` for output links

0.3.0:
- Complete rewrite, rename project
- Grab releases from AS Forums
- Network concurrency: 16
- External search spec `search.edn`
- Strips off trackers from magnet URI

0.2.0:
- Auto-detect last page
- Prints progress to STDERR

0.1.0:
- Initial release

### Download

Latest Release: [as_magnet_extract][]

### Usage

Prerequisite:

- [erlang][]

```
$ escript as_magnet_extract           ## reads search.edn from current dir
$ escript as_magnet_extract 64265316  ## provide an AS search ID
```

See provided `search.yml` for example config or get your own search ID from AS Forums.

e.g.
```
$ escript as_magnet_extract
```

STDERR:
```
Search ID: 64265316
Found 105 threads in 5 pages.
...
```

STDOUT:
```
magnet:?xt=urn:btih:1BE334D113C13F0D522CEFCFFC37B2833E1C513C
magnet:?xt=urn:btih:1F9092B345ED9F27A3F900FC9D2A5774B899F133
magnet:?xt=urn:btih:476C6191D86AF5B99F669004AC4A0C24A6DC897B
magnet:?xt=urn:btih:5D1FF54628C2B3C6BB141E6EA6F5A4655F9487AA
magnet:?xt=urn:btih:DFFD91874922EFFF512F8C9E39AE5C03DF5B9A3F
magnet:?xt=urn:btih:6B535D95D61B2395801DF9F0FBD8B66E5E448808
magnet:?xt=urn:btih:422EB714E9CEB624881EF2683438C59F8DB29472
...
```

### Building

Prerequisite:

- git
- [mix][]
  - elixir
  - erlang

```
$ git clone ...
$ cd as-magnet-extract
$ mix deps.get
$ mix escript.build
```

[as_magnet_extract]: https://github.com/akiroz/as-magnet-extract/releases/download/0.4.1/as_magnet_extract
[erlang]: http://www.erlang.org/ 
[mix]: https://elixir-lang.org/getting-started/mix-otp/introduction-to-mix.html

