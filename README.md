# Description
This is an academic project for subject SNT. The implemented program resolve Capacitated Vehicle Routing Problem [VRP](https://en.wikipedia.org/wiki/Vehicle_routing_problem).
For resolving problem  ALNS and Tabu-search approaches were used. Implemented program works based on algorithm described in the article [A novel heuristic algorithm for capacitated vehicle routing problem](https://link.springer.com/content/pdf/10.1007%2Fs40092-017-0187-9.pdf)
but with modifications inspire by article [A general heuristic for vehicle routing problems](https://www.sciencedirect.com/science/article/pii/S0305054805003023).

First step of described algorithm was replace by simple [greedy search](https://en.wikipedia.org/wiki/Greedy_algorithm).
Moreover strategy for looking better place for selected vertex was added. More details about it can be found in documentation [documentation](https://www.overleaf.com/read/tyjqkfzrxyvf)( Czech language).

# Compilation
For compilation the [maven](https://maven.apache.org) was used, Java 1.8 is also required.
The `mvn package` command compile the source code and create executable `.jar` archive.

In case of ones faced with an error `Error occurred during initialization of VM Could not allocate metaspace: N bytes`
it is necessary to increase limit of memory usage by typing `ulimit -v N`.

After compilation the executable archive `target/SNT-VRP-1.0-SNAPSHOT.jar` will be created.

# Running program
There is number of parameters can be used for running the program.
The program is ran by command `java -jar target/SNT-VRP-1.0-SNAPSHOT.jar -p path/to/instance.xml [-c] [-i I] [-g] [-h H] [-d D]`
1. `-p path/to/instance.xml` defines path to problem instance which is supposed to be an XML file
2. `-c` switch runs program in 'comparison' mode. The solution of the simple _greedy search_ algorithm will be also printed
3. `-i I` set amount of iterations used as stop condition(default value is 300)
4. `-g` switch plot the graph of the routes
5. `-h H` horizon defines the least amount of iterations while solution is placed in the Tabu-list
6. `-d D` value `H + D` defines the most amount of iterations while solution is placed in the Tabu-list

# Output description
The output has format:

`Total demand: TD`

 `Vehicle: 0 vehicle capacity: C load:L route: (id: 0 oX: X oY: Y)->[(id: i oX: X oY: Y)]->(id: 0 oX: X oY: X)`
 
 `...`
 
 `Vehicle: N-1 vehicle capacity: C load:L route: (id: 0 oX: X oY: Y)->[(id: i oX: X oY: Y)]->(id: 0 oX: X oY: X)`
 
 `Total cost: TC`
 
where:
* `Total demand: TD` is a sum of all customer's demands
* `Vehicle: id` is a identificator of vehicle
* `vehicle capacity: C` is a max capacity of vehicle
* `loca: L` is a used capacity on the route
* `route: ` is a vehicles planned route
* `(id: 0 oX: X oY: Y)` is a location of the depot
* `[(id: i oX: X oY: Y)]` list of locations planned to visit
* `Total cost: TC` sum of all distances of planned routes 

# Data set
Data-set was used for experiments is locate in `src/test/dataset/input` directory.
Each instance is an XML document described topology.
Each instance defines:
1. set of nodes and their locations
2. characteristics of vehicles
3. demand of each nodes

The [VRP-REP](http://www.vrp-rep.org) was used as source of data.

# Experiments
For executing automatic tests `src/test/test.sh` script is used. Script is ran using `bash test.sh [--iterations I] [--horizon H] [--out output/dir] [--input instances/] [--snapshot path/SNT-VRP-1.0-SNAPSHOT.jar] [--distance D] [--it II]`
where:
1. `--iterations I` is equivalent to `-i I` from [here](#Running program)
2. `--horizon H` is equivalent to `-h H` from [here](#Running program)
3. `--out output/dir` path to directory where the results will be stored(default is `./output/`)
4. `--input instances/` path to directory containing the instances(default is `./dataset/input/`)
5. `--snapshot path/SNT-VRP-1.0-SNAPSHOT.jar` path to executable `.jar` file(default is `target/SNT-VRP-1.0-SNAPSHOT.jar`)
6. `--distance D` is equivalent to `-d D` from [here](#Running program)
7. `--it II` defines number of experiments with single instance(default is 6)


First of all script compile the source code using `mvn packge`(check [this](#Compilation) to avoid problems).

Basically script can be ran with default parameters from default directory which is `src/test/`, no need to change them.

After completing the file `summarize.txt` contains description of experiment's conditions. 



# Results of experiments
Because of restrictions on submitted archive results of experiments can be found on google drive [here](https://drive.google.com/file/d/1vP_lmZc5K1hn4WHfW6IW5ZZW3zkYO8Rn/view?usp=sharing).
