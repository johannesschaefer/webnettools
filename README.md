# Web Net Tools

Web Net Tools are an web frontend for some useful tooling. It provides especially an web frontend for tools like testssl.sh and nmap.
Currently it support the following tools:
* Ping
* Traceroute
* [Nmap](https://nmap.org)
* [testssl.sh](https://github.com/drwetter/testssl.sh)

<p align="center">
  <img align="center" style="display: inline" src="docs/testssl.png" alt="test ssl tooling" height="400" />
  <img align="center" style="display: inline" src="docs/ping.png" alt="ping tooling" height="400" />
</p>

Web Net Tools is a ready to use Docker image. No additional setup of the mentioned tools is needed.

## Usage

Simple run the following Docker command:
```
docker run -p 8080:8080 --name webnettools johannesschafer/webnettools:0.0.1-SNAPSHOT
```

## Configuration options

Currently you can configure the following aspects

* Available tools - set the Docker environment variable `AVAILABLE_TOOLS` with an set of the following tools to limit the tools shown in the web frontend: `testssl`, `ping`, `traceroute`, `nmap`. E.g. `AVAILABLE_TOOLS=testssl,nmap`.

* Rate limit - Limits the number of calls of a specific tool to a specific target (e.g. host). The value is set in milliseconds between consecutive calls. E.g. set Docker variable `RATE_LIMIT=60000` to limit the calls to one per minute.

* To set additional certificates for the testssl.sh tool, just map the folder with the certificates to the container folder /certs. E.g.
```
docker run -p 8080:8080 -v ./localfolderwithcerts:/certs --name webnettools johannesschafer/webnettools:0.0.1-SNAPSHOT
```