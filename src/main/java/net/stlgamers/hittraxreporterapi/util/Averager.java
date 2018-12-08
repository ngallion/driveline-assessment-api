package net.stlgamers.hittraxreporterapi.util;

public class Averager {
    private final Integer total;
    private final Integer count;

    public Averager() {
        this.total = 0;
        this.count = 0;
    }

    public Averager(Integer total, Integer count) {
        this.total = total;
        this.count = count;
    }

    public double average() {
        return count > 0 ? ((double) total) / count : 0;
    }

    public Averager accept(Integer i) {
        return new Averager(total + i, count + 1);
    }

    public Averager combine(Averager other) {
        return new Averager(total + other.total, count + other.count);
    }
}