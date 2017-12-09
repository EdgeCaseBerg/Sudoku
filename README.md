# Sudoku

## Eh?

I got bored in an airplane, so I grabbed the magazine from the seat pocket in 
front of me and read a few articles and then saw that they had sudoku in the 
back! But sadly, there was no pen or pencil in the pocket. But I had a laptop
with me! So I decided that I'd just make a console game for fun.

## Ok... How do I play?

Assuming you've got sbt installed go ahead and do:

```
$ sbt
sbt> console
scala> Game.newGame()
```

And then you'll see something like:

```
Creating new game...
Game ready! Have fun!
res0: Grid =

 * | * | * | * | * | * | * | *
---+---+---+---+---+---+---+---
 * | 1 |   |   | 5 |   |   | *
---+---+---+---+---+---+---+---
 * | 2 |   |   |   |   | 6 | *
---+---+---+---+---+---+---+---
 * |   | 6 |   | 2 |   |   | *
---+---+---+---+---+---+---+---
 * |   |   |   |   | 3 | 5 | *
---+---+---+---+---+---+---+---
 * |   | 5 |   |   |   | 1 | *
---+---+---+---+---+---+---+---
 * |   | 2 | 6 |   |   |   | *
---+---+---+---+---+---+---+---
 * | * | * | * | * | * | * | *
---+---+---+---+---+---+---+---
```

From then on you can call a method on your `res#` to make a change. 
The methods you'll need to use are:


- `viewBoard`: Prints the board to the console
- `fillBoxAt((x: Int,y: Int), value: Int)`: Places a value into the grid at the location you want it to be at
- `eraseBoxAt((x: Int,y: Int))`: Removes a value in the grid at the location you want it to be at
- `isSolutionValid_?`: Returns true or false if your solution is currently valid or not.

As you might have noticed the default is a 6 by 6 sudoku grid because it fit better on my console than a 9x9.

