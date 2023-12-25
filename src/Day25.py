from math import prod
from igraph import Graph

G = {
    v: e.split()
    for v, e in [l.split(':')
                 for l in open('Day25.txt')]
}

print(G)

sizes = Graph.ListDict(G).mincut().sizes()
assert len(sizes) == 2
print(prod(sizes))
