# Making a Game with Prospero and Iris

# Tutorial 1: Dealing with 2D Sprites

## What we'll Learn here

We're going to dig into building core games objects in detail in this tutorial.
We'll explain a bit about the game tree and set up our first, simple sprite-based
graphic.

At the end of this tutorial, we'll have created the first enemy for the game and
we'll use them to create the basis of the title page we'll set up in the next
tutorial.

## Organize our Game

We are going to build the start of the enemies for our game now.  We're also
going to reuse the enemy model to display the title of the game.  (See Phoenix
for the inspiration)

Let's start by carving out a namespace to manage the enemies.  Create bugs.enemies
and start it with this declaration:

```
(ns bugs.enemies
  (:require [prospero.game-objects :as progo]))
```

We only need `prospero.game-objects` right now and it is, again, used to provide
convenience builder functions for configuring our game objects.

Building a namespace like this to manage each aspect of the game is going to
make it a lot easier to navigate our code as the game grows.  It's certainly ok
to experiment with other options for organizing the code, but that's outside
the scope of this tutorial.

Just for now, we'll also introduce a `def` to keep track of the size of our
game elements:

```
(def tile-size 64)
```

Later we can move this to a common, config location.  It will be fine in the
`enemies` namespace for now.

## Building our first Sprite

Now let's make a function to build our first enemy:

```
(defn make-bug
  [game-system x-loc y-loc]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   tile-size tile-size)
      (progo/sprite-sheet {:sprite-sheet-path "/images/sprite_sheet.png"
                           :sprite-width      tile-size
                           :sprite-height     tile-size
                           :columns           5
                           :rows              5})
      (progo/sprite-index 0 0)
      (progo/position-3d  x-loc y-loc 0)))
```

If you're eager to see the results, open `core.cljs` and replace the
code for the red box with this:

```
(procore/start-game game-system
                    [(enemies/make-bug game-system 60 10)])
```

Just remember to `:require` `bugs.enemies` into the namespace.
If you've still got figwheel running, you should see a single bug in
your browser window.

## Understanding the Pieces of make-bug

All game objects start with `(progo/base-object  game-system)`.  Internally,
this just makes sure *Prospero* always has an available reference to the
initial configuration of the game.

The `sprite-sheet` call provides the information the game-object needs to
pull textures from an image made up of many tiles.  Our sprites let us
pack many images for the game into a single image and we can animate objects
by selecting different parts of the image.  `sprite-index` tells the game
object to use the sprite information to pick a texture at the supplied
row and column index.  Together, these two functions make it a lot easier
to manage and provide textures in 2d games.


 `(progo/bounds-box tile-size tile-size)` is used to set the extent of the
 game object.  This is used by the sprite system to limit the amount of
 the texture that's displayed.  It's possible to use different bounds from
 the sprite `:width` and `:height`, but for most games they'll be the same.
 These bounds can be different from the bounds used for collisions and other
 parts of the game system, but they will be used as defaults.

The last part, `(progo/position-3d  x-loc y-loc 0)` positions the game object
on the screen.  We're using `position-3d` so we can control how objects layer
on top of each other.  The position arguments can be any positive or negative
number, allowing us to position items off screen, too.

These basic tools are most of what we need to provide enemies in game.
Now we'll create a new game object to provide some organization around a
lot of bugs.

## Organizing the Bugs

This is probably a good time to talk about how game objects are organized.
Game objects are passed to *Prospero* in the `start-game` call in a vector.
Since all Game Objects can have children, objects are effectively in a
directed acyclic graph.  We'll often refer to this as the game tree, but
that's more convenience than precision.

To manage the bugs, we're going to make a game object that holds an entire
collection of them.  This will help us later, as we can move the collection
game object on screen and that will move all the bugs at the same time.
Whenever we use `position-3d` or similar functions, the position we are
specifying is always in the position of the object's parent.   Any
transformations to position, rotation, and scale are carried on "down" the
tree.

We'll make an object to hold the bugs like so:
```
(defn make-bug-manager
  [game-system]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   (:display-width game-system)
                          (:display-height game-system))
      (progo/position-3d  0 0 0)))
```

This doesn't do much now, but we'll see how to add and remove enemies from
it in a future tutorial.  Just know that it's a mostly good practice to
provide some organizational structure like this.

Let's head over to core.cljs and switch to the `make-bug-manager` function.
This will remove the bug we've had on screen.

## Demonstrating the Game Object Hierarchy

Next we'll make a new namespace that will let us create letters from our bugs.
This will demonstrate how the game tree works as a hierarchy to lay objects
out on screen.

Let's create a new namespace:

```
(ns bugs.sprite-font
  (:require [bugs.enemies :as enemies]
            [prospero.game-objects :as progo]))
```

This multimethod just provides the positions for the sprites that make our
letters:
```
(defmulti letter-pos (fn [letter] letter))

(defmethod letter-pos :t
  [_]
  [[  0   0]
   [ 64   0]
   [128   0]
   [192   0]
   [256   0]
   [128  64]
   [128 128]
   [128 192]
   [128 256]])
```

This function builds a letter:
```
(defn letter
  [game-system x-loc y-loc z-loc letter]
  (-> (progo/base-object game-system)
      (progo/position-3d x-loc y-loc z-loc)
      (assoc :children
             (map
              (fn [[x y]]
                (enemies/make-bug game-system x y))
              (letter-pos letter)))))
```

Notice that the `x-loc`, `y-loc` parameters are different from the `x` and `y` of
the individual `make-bug` calls.  This lets us lay out the bug components of the
letters relative to the top-level position of our letter.  With this property of
the game tree, we can make fine adjustments to the local position of objects or
create templates like these letters that can be positioned anywhere on screen.


## Ending with some bugs

Before we're done here, update the manager with the following:
```
(defn make-bug-manager
  [game-system]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   (:display-width game-system)
                          (:display-height game-system))
      (progo/position-3d  0 0 0)
      (assoc :children [])))
```

This will prepare it for holding our enemies in an upcoming tutorial.

Now update the `core` ns with the following:

```
(procore/start-game game-system
                    [(sprite-font/letter game-system  30  10 1 :t)
                     (sprite-font/letter game-system 386  10 1 :h)
                     (sprite-font/letter game-system 686  10 1 :e)
                     (sprite-font/letter game-system   0 350 1 :b)
                     (sprite-font/letter game-system 250 350 1 :u)
                     (sprite-font/letter game-system 509 350 1 :g)
                     (sprite-font/letter game-system 769 350 1 :s)
                     (enemies/make-bug-manager game-system)])
```

When you save the file, you should see a quick message.  Later we'll refine this
technique to make this sort of thing a lot easier.

The key observation of that last change is to see the use of `clojure.core/assoc`.
Since the nodes in our game tree are just Clojure maps, we can also use `assoc`
and other Clojure functions to manipulate the game tree.

## Recap

In tutorial 0, we set up the basic environment for a game.  Now we've created a
static enemy and a little bit of structure to manage them.  This doesn't seem
like  a lot, but we've got the basics of 2d sprites, now.  You can try moving
them around by supplying a `progo/update-state` function that updates
`progo/position-3d` to move the sprites around (although we'll look at animations
shortly).


## What's Next
Now that we've created an enemy and the beginnings of a title page, we'll build
on that in the next tutorial by creating the handlers for the game's major modes.
This will give us a game screen, title pages, and support for pause.
