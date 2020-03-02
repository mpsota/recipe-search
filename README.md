# Recipe search

Program solving the recipe search problem.

## Essential business stakeholder and technical requirements:

- Search results should be relevant, e.g. a search for broccoli stilton soup should return at least broccoli stilton soup.
- Searches should complete quickly so users are not kept waiting – this tool needs to serve many users so lower latency will mean we can serve more concurrent searches - ideally searches will take < 10ms.
- Ideally the results will be sorted so that the most relevant result is first in the result list.

- The name of each file is considered the id of the recipe.

- Returning relevant results is fairly open ended and subjective, so there is a lot of room for increasing sophistication.
- Performance / Resource utilisation is another area where you can apply more sophistication to the solution.
- Degree to which the solution is generalized without loss of clarity is another.

- Be reasonable in your use of libraries or existing search tools – In this test we really do want you to reinvent the wheel as far as the core problem is concerned!
- Just using grep will not win you many points. Feel free to use established techniques and algorithms, in fact this is encouraged.

## Usage

Copy `profile.clj.example` as `profile.clj` so the configuration suits your needs.
Without `profiles.clj` default configuration from `project.clj` will be used (api on `3001`). 

### REPL
```
$ lein repl
dev=> (start) ;; takes around 15 seconds to initialize the DB
#<SystemMap>
dev=> (ns recipe-search.search)
search=> (time (search (:recipes-db system.repl/system) ["lasagne" "tomatoes" "olives" "onion"] [::rss/id ::rss/raw-text]))
"Elapsed time: 0.468183 msecs"
=>

=> (db/get-recipe (:recipes-db system.repl/system) "fennel-mushroom-lasagne.txt")
(#:recipe-search.spec{:id "fennel-mushroom-lasagne.txt",
                      :raw-text "Fennel and mushroom lasagne
                                 Introduction: ....})
```

### Web browser

```
$ npm install
$ npx shadow-cljs release app
$ lein run
Enter http://localhost:3001/index.html
```

## License

Copyright © 2020 Michał Psota

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
