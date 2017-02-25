# clj-consul-catalog

Consul client for registering and deregistring external service with clojure

## Installation

### Leiningen/Boot

```clojure
[![Clojars Project](https://img.shields.io/clojars/v/clj-consul-catalog.svg)](https://clojars.org/clj-consul-catalog)
```

## Usage

```clojure
(:require [clj-consul-catalog.core :refer [service discover register deregister]])
```

## Examples


```clojure
(def path "http://localhost:8500/v1/catalog/")
(def params {:node    "DESKTOP-2RC0A0R"
             :address "127.0.0.1"
             :service {
                       :id "redis1"
                       :service "redis"
                       :address "127.0.0.1"
                       :port 8080}}

(def s (service path params))
```

optional parameter :interval <value> can be added to service method to re-register the service

```clojure

(def s (service path params :interval 10))

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
