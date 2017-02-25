# clj-consul-catalog

Consul client for registering and deregistring external service with clojure

## Installation

### Leiningen/Boot

```clojure
[clj-consul-catalog "0.1.0-SNAPSHOT"]
```

## Usage

```clojure
(:require [clj-consul-catalog.core :refer [service discover register deregister]])
```

## Examples


```clojure
(def s (service
                "http://localhost:8500/v1/catalog/"
                {:node    "DESKTOP-2RC0A0R"
                   :address "127.0.0.1"
                   :service {
                             :id "redis1"
                             :service "redis"
                             :address "127.0.0.1"
                             :port 8080}}))
```

```clojure
(register s)
```
```clojure
(deregister s)
```

```clojure
(discover s)
```




## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
