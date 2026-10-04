import csv
import math
import os
import sys
from collections import defaultdict

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

RESULTS = sys.argv[1] if len(sys.argv) > 1 else "results/results.csv"
MEMORY = sys.argv[2] if len(sys.argv) > 2 else "results/memory.csv"
OUT = sys.argv[3] if len(sys.argv) > 3 else "results/plots"

COLORS = {"DynamicArray": "#1f77b4", "MyLinkedList": "#d62728", "MinHeap": "#2ca02c"}
MARKERS = {"DynamicArray": "o", "MyLinkedList": "s", "MinHeap": "^"}


def read(path):
    with open(path, newline="") as handle:
        return list(csv.DictReader(handle))


def series(rows, workload, field):
    grouped = defaultdict(list)
    for row in rows:
        if row["workload"] == workload:
            grouped[row["structure"]].append((int(row["n"]), float(row[field])))
    for key in grouped:
        grouped[key].sort()
    return grouped


def finish(fig, ax, title, xlabel, ylabel, name, logx=True, logy=True):
    if logx:
        ax.set_xscale("log")
    if logy:
        ax.set_yscale("log")
    ax.set_title(title)
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    ax.grid(True, which="both", alpha=0.3)
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(OUT, name), dpi=150)
    plt.close(fig)


def plot_lines(lines, title, xlabel, ylabel, name, logy=True):
    fig, ax = plt.subplots(figsize=(7, 4.5))
    for label, color, marker, points in lines:
        ax.plot([p[0] for p in points], [p[1] for p in points], marker=marker, color=color, label=label)
    finish(fig, ax, title, xlabel, ylabel, name, logy=logy)


def structure_lines(rows, workload, field):
    data = series(rows, workload, field)
    return [(name, COLORS[name], MARKERS[name], pts) for name, pts in data.items()]


def main():
    os.makedirs(OUT, exist_ok=True)
    rows = read(RESULTS)

    plot_lines(structure_lines(rows, "W1_append", "median_time_ms"),
               "W1: append n elements", "n (elements)", "median time (ms)", "w1_append_time.png")
    plot_lines(structure_lines(rows, "W2_random_get", "median_time_ms"),
               "W2: 1000 random get(i) calls", "n (list size)", "median time (ms)", "w2_random_get_time.png")
    plot_lines(structure_lines(rows, "W2_random_get", "steps"),
               "W2: steps counted in 1000 random get(i) calls", "n (list size)", "steps (nodes/slots visited)",
               "w2_random_get_steps.png")
    plot_lines(structure_lines(rows, "W3a_insert_front", "median_time_ms"),
               "W3a: n inserts at the front", "n (inserts)", "median time (ms)", "w3a_insert_front_time.png")
    plot_lines(structure_lines(rows, "W3b_insert_random", "median_time_ms"),
               "W3b: n inserts at random positions", "n (inserts)", "median time (ms)", "w3b_insert_random_time.png")

    fig, ax = plt.subplots(figsize=(7, 4.5))
    for name in ("DynamicArray", "MyLinkedList"):
        moves = series(rows, "W3b_insert_random", "moves")[name]
        steps = series(rows, "W3b_insert_random", "steps")[name]
        ax.plot([p[0] for p in moves], [p[1] for p in moves], marker=MARKERS[name], color=COLORS[name],
                linestyle="-", label=name + " moves")
        ax.plot([p[0] for p in steps], [p[1] for p in steps], marker=MARKERS[name], color=COLORS[name],
                linestyle="--", label=name + " steps")
    finish(fig, ax, "W3b: counted work for n random inserts", "n (inserts)", "counted operations (count)",
           "w3b_counters.png")

    fig, ax = plt.subplots(figsize=(7, 4.5))
    for workload, style, label in (("W4a_heap_insert", "-", "insert"), ("W4b_heap_extract_all", "--", "extractMin")):
        pts = series(rows, workload, "median_time_ms")["MinHeap"]
        ax.plot([p[0] for p in pts], [p[1] for p in pts], marker="^", color=COLORS["MinHeap"],
                linestyle=style, label="MinHeap " + label + " x n")
    finish(fig, ax, "W4: MinHeap, n operations", "n (operations)", "median time (ms)", "w4_heap_time.png")

    fig, ax = plt.subplots(figsize=(7, 4.5))
    for workload, style, label in (("W4a_heap_insert", "-", "insert"), ("W4b_heap_extract_all", "--", "extractMin")):
        pts = series(rows, workload, "comparisons")["MinHeap"]
        ax.plot([p[0] for p in pts], [p[1] / p[0] for p in pts], marker="^", color=COLORS["MinHeap"],
                linestyle=style, label="MinHeap " + label)
    ns = [1000, 10000, 100000, 1000000]
    ax.plot(ns, [2 * math.log2(n) for n in ns], color="gray", linestyle=":", label="2 log2 n (extractMin bound)")
    finish(fig, ax, "W4: comparisons per operation", "n (heap size)", "comparisons per operation (count)",
           "w4_heap_comparisons.png", logy=False)

    fig, ax = plt.subplots(figsize=(7, 4.5))
    for workload, color, marker, label in (("W5a_build_by_insert", "#9467bd", "o", "n x insert(x)"),
                                           ("W5b_build_floyd", "#ff7f0e", "s", "buildHeap (Floyd)")):
        pts = series(rows, workload, "median_time_ms")["MinHeap"]
        ax.plot([p[0] for p in pts], [p[1] for p in pts], marker=marker, color=color, label=label)
    finish(fig, ax, "W5: building a heap of n elements", "n (elements)", "median time (ms)", "w5_build_time.png")

    fig, ax = plt.subplots(figsize=(7, 4.5))
    for workload, color, marker, label in (("W5a_build_by_insert", "#9467bd", "o", "n x insert(x)"),
                                           ("W5b_build_floyd", "#ff7f0e", "s", "buildHeap (Floyd)")):
        pts = series(rows, workload, "comparisons")["MinHeap"]
        ax.plot([p[0] for p in pts], [p[1] / p[0] for p in pts], marker=marker, color=color, label=label)
    finish(fig, ax, "W5: comparisons per element while building", "n (elements)",
           "comparisons per element (count)", "w5_build_comparisons.png", logy=False)

    memory = read(MEMORY)
    fig, ax = plt.subplots(figsize=(7, 4.5))
    grouped = defaultdict(list)
    for row in memory:
        grouped[row["structure"]].append((int(row["n"]), float(row["megabytes"])))
    for name, pts in grouped.items():
        pts.sort()
        ax.plot([p[0] for p in pts], [p[1] for p in pts], marker=MARKERS[name], color=COLORS[name],
                linestyle="--" if name == "MinHeap" else "-", label=name)
    finish(fig, ax, "Memory footprint measured with JOL", "n (elements)", "memory (MB)", "memory_mb.png")


main()
