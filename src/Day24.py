from sympy import Symbol
from sympy import solve_poly_system

# This solves part 2 using a system of equations and SymPy

hailstones = []
for line in open("Day24.txt", "r"):
    pos, vel = line.strip().split(" @ ")
    px, py, pz = pos.split(", ")
    x_vel, y_vel, z_vel = vel.split(", ")
    hailstones.append((int(px), int(py), int(pz), int(x_vel), int(y_vel), int(z_vel)))
    pass

# Part 2
#  Throw:
#     x_throw(t) = x_throw + time * x_vel_throw
#     y_throw(t) = y_throw + time * y_vel_throw
#     z_throw(t) = z_throw + time * z_vel_throw
#  Equations for intersection of throw and hailstones:
#     x_throw + time * x_vel_throw = x_line_1 + time * x_vel_line1
#     y_throw + time * y_vel_throw = y_line_1 + time * y_vel_line1
#     z_throw + time * z_vel_throw = z_line_1 + time * z_vel_line1

#     x_throw + time * x_vel_throw = x_line_2 + time * x_vel_line2
#     y_throw + time * y_vel_throw = y_line_2 + time * y_vel_line2
#     z_throw + time * z_vel_throw = z_line_2 + time * z_vel_line2

#     x_throw + time * x_vel_throw = x_line_2 + time * x_vel_line3
#     y_throw + time * y_vel_throw = y_line_2 + time * y_vel_line3
#     z_throw + time * z_vel_throw = z_line_2 + time * z_vel_line3
#   After 3 hailstones we have 9 variables and 9 equations. That's enough to solve this.

x_throw = Symbol('x_throw')
y_throw = Symbol('y_throw')
z_throw = Symbol('z_throw')
x_vel_throw = Symbol('x_vel_throw')
y_vel_throw = Symbol('y_vel_throw')
z_vel_throw = Symbol('z_vel_throw')

equations = []
time_symbols = []
# the secret sauce is that once you have three shards to intersect, there's only one valid line
# so we don't have to set up a huge system of equations that would take forever to solve. Just pick the first three.
for idx, hailstone in enumerate(hailstones[:3]):
    x_line, y_line, z_line, x_vel_line, y_vel_line, z_vel_line = hailstone
    time = Symbol('time_' + str(idx))

    # time_1 = (x_line_1 - x_throw) / (x_vel_throw / x_vel_line1)
    eqx = x_throw + x_vel_throw * time - x_line - x_vel_line * time
    eqy = y_throw + y_vel_throw * time - y_line - y_vel_line * time
    eqz = z_throw + z_vel_throw * time - z_line - z_vel_line * time

    equations.append(eqx)
    equations.append(eqy)
    equations.append(eqz)
    time_symbols.append(time)
    pass

symbols = [x_throw, y_throw, z_throw, x_vel_throw, y_vel_throw, z_vel_throw] + time_symbols
result = solve_poly_system(
    equations,
    *symbols
)
print(list(zip(
    [symbol.name for symbol in symbols],
    result[0]
)))
print(result[0][0] + result[0][1] + result[0][2])  # part 2 answer
