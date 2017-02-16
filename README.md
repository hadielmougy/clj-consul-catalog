# clj-consul-catalog

Consul client for registering and deregistring external service with clojure

## Installation

### Leiningen/Boot

```clojure
[clj-consul-catalog "0.1.0-SNAPSHOT"]
```

## Usage

(:require [clj-consul-catalog.core :refer [register deregister]])

## Examples
```clojure
(register "http://localhost:8500/v1/catalog/" 
        {:node    "DESKTOP-2RC0A0R"
        :address "127.0.0.1"
        :service {
                    :id "redis1"
                    :service "redis"
                    :address "127.0.0.1"
                    :port 8000}
        })
```
```clojure
(deregister "http://localhost:8500/v1/catalog/"
            {:datacenter "dc1"
             :node "DESKTOP-2RC0A0R"
             :service-id "redis1"})
```




## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
