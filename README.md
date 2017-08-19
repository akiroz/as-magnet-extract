## girlcelly-magnet-extract

Simple script for grabbing magnet URLs from girlcelly.blog

Written in ~ 30 lines of Clojure, no error handling.

### Changes

0.2.0:
- Auto-detect last page
- Prints progress to STDERR

0.1.0:
- Initial release

### Download

Latest Release: [girlcelly-magnet-extract.jar][]

[girlcelly-magnet-extract.jar]: https://github.com/akiroz/girlcelly-magnet-extract/releases/download/0.2.0/girlcelly-magnet-extract.jar

### Usage

```
$ java -jar girlcelly-magnet-extract.jar <archive>
```

e.g.
```
$ java -jar girlcelly-magnet-extract.jar blog-date-201704
```
STDERR:
```
Requesting page 0... found 10 URLs.
Requesting page 1... found 10 URLs.
Requesting page 2...
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
$ cd girlcelly-magnet-extract
$ lein uberjar
```

Runable JAR is in `target/uberjar/`
