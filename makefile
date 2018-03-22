COMPILER=mvn

package: increase_limit
	mvn package

increase_limit:
	ulimit -v 1073741824


