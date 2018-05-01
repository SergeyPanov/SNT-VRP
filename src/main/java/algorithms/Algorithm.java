package algorithms;

import environment.Environment;

/**
 * Interface should be implemented by any algorithm.
 */
public interface Algorithm {
    Environment execute(Environment environment);
}
