package benchmark;

import metrics.Metrics;

abstract class Scenario {

    private final String workload;
    private final String structure;
    private final int n;
    private final long operations;

    Scenario(String workload, String structure, int n, long operations) {
        this.workload = workload;
        this.structure = structure;
        this.n = n;
        this.operations = operations;
    }

    String workload() {
        return workload;
    }

    String structure() {
        return structure;
    }

    int n() {
        return n;
    }

    long operations() {
        return operations;
    }

    abstract void prepare();

    abstract long execute();

    abstract Metrics metrics();
}
