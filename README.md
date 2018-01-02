## as-magnet-extract

Simple script for grabbing magnet URIs from AS Forums

### Changes

0.3.0:
- Complete rewrite, rename project
- Grab releases from AS Forums
- Network concurrency: 16
- External search spec `search.edn`

0.2.0:
- Auto-detect last page
- Prints progress to STDERR

0.1.0:
- Initial release

### Download

Latest Release: [as-magnet-extract.jar][]

[as-magnet-extract.jar]: https://github.com/akiroz/as-magnet-extract/releases/download/0.3.0/as-magnet-extract.jar

### Usage

```
$ java -jar as-magnet-extract.jar           ## reads search.edn from current dir
$ java -jar as-magnet-extract.jar 64265316  ## provide an AS search ID
```

e.g.
```
$ java -jar as-magnet-extract.jar
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

```
$ git clone ...
$ cd as-magnet-extract
$ lein uberjar
```

Runable JAR is in `target/uberjar/`
