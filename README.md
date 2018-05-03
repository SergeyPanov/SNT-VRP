# Description
This is an academic project for SNT subject. The implemented program resolve Capacitated Vehicle Routing Problem [VRP](https://en.wikipedia.org/wiki/Vehicle_routing_problem).
For resolving problem  ALNS and Tabu-search approaches were used. Implemented program works base on novel algorithm described in article [A novel heuristic algorithm for capacitated vehicle routing problem](https://link.springer.com/content/pdf/10.1007%2Fs40092-017-0187-9.pdf)
but with modifications inspire by article [A general heuristic for vehicle routing problems](https://www.sciencedirect.com/science/article/pii/S0305054805003023).

The first step of described algorithm was replace by simple [greedy search](https://en.wikipedia.org/wiki/Greedy_algorithm).
Moreover strategy for looking better place for selected vertex was added. More details about it can be found in documentation [documentation](https://www.overleaf.com/read/tyjqkfzrxyvf)( Czech language).

# Compilation
For compilation the [maven](https://maven.apache.org) was used, Java 1.8 also is required.
The `mvn package` command compile the source code and create executable `.jar` archive.

In case of ones faced with an error `Error occurred during initialization of VM Could not allocate metaspace: N bytes`
it is necessary to increase limit of memory usage by typing `unlimit -v N`
