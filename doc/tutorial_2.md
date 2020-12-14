# Making a Game with Prospero and Iris

# Tutorial 2: Major Modes and Screens

## What we'll Learn here

We're still in preliminaries here.  So far we've seen the work we need to get
up and running and the rudiments of the game tree.  This section of the tutorial
covers how to set up major game modes.  Major game modes will let us switch
screens but they're essential for supporting pause.

## Planning Ahead For Different Input Modes

Before starting a game, it's worth spending a bit of hammock time to figure out
the major features and functions.  Part of this is planning for menus, pause,
and so on.  *Prospero* keeps track of one state item in the game that represents
the games main state, and (to keep the engine mostly functional) passes this
state argument to most of its functions.

Most systems in a game can be supplied with a set of valid states (typically
keywords) that they will run in.  This lets us turn off animations and
collisions when a game is paused, for instance.

It's worth planning for this up front.

For this game, we'll create the following screens and modes:

 - main title screen (:title-mode)
 - gameplay screen (:running)
 - gameplay screen/paused (:paused)
 - game-over screen (:game-over)

 With this rough plan in our heads, we'll proceed to lay out the structure for
 switching screens.

## Building some Facilities for Screens

We'll start by building an infrastructure to handle screen switching.  We can build
some new namespaces now, so they're ready for Figwheel when we tie them into `core`.

Let's make a `bugs.screens` namespace that will hold a multimethod for returning
screen children:

```
(ns bugs.screens)

(defmulti screen-children (fn [game-mode game-system] game-mode))
```

This should be pretty self explanatory.

Now we'll make a namespace for our title page:

```
(ns bugs.screens.title
  (:require [bugs.screens :as screen]
            [bugs.sprite-font :as sprite-font]))


(defmethod screen/screen-children :title-screen
  [code game-system]
  [(sprite-font/letter game-system  30  10 1 :t)
   (sprite-font/letter game-system 386  10 1 :h)
   (sprite-font/letter game-system 686  10 1 :e)
   (sprite-font/letter game-system   0 350 1 :b)
   (sprite-font/letter game-system 250 350 1 :u)
   (sprite-font/letter game-system 509 350 1 :g)
   (sprite-font/letter game-system 769 350 1 :s)])

```

This is just the code that was in `core` from the end of the last tutorial.


## A Game Root with Screen Switching

We're going to update the `core` namespace so that it will handle switching
screens, now.

We will add `[bugs.screens :as screens]`, `[bugs.screens.title :as title]`,
and `[prospero.loop :as proloop]` to our require to pull in the code we just
wrote and one other thing we need.

Next we'll update the game system to tell *Prospero* we want to use a
global mode:

```
(def game-system {::procore/game-system      ::iris/game-system
                  ::proloop/global-mode-path [0 :global-mode]
                  :display-width             1024
                  :display-height             768
                  ::iris/web-root            (.getElementById js/document "app")})
```

This new key, `::proloop/global-mode-path [0 :global-mode]`, sets up *Prospero*
to find and supply a global-mode to its subsystems at each frame.  The `0` in
the vector tells it to look in the first child at the root (more on that in  a
moment).

Now we'll write a `make-root` function that creates our game root object:

```
(defn make-root
  [game-system]
  (->
   (progo/base-object   game-system)
   (assoc               :children     [])
   (assoc               :global-mode  :title-screen)
   (assoc               :current-mode :boot-up)
   (progo/update-state (fn [me current-time-code frame-elapsed-time root-state global-mode]
                         (let [children     (:children me)
                               current-mode (:current-mode me)]

                           (if (= current-mode global-mode)
                             me
                             (do
                               (cond-> me
                                 true
                                 (assoc :current-mode global-mode)

                                 (not= global-mode :paused)
                                 (assoc :children (screens/screen-children global-mode game-system))))))))))
```

We've seen most of this before, but the `progo/update-state` function is new and
responsible for the screen-switch magic.  `update-state` is called by *Prospero*
at each loop for each game object that has a supplied function.  We've left
the full names of parameters we would normally just mark as unused with `_`, just to
see what is supplied to the function.

We've set up this root object to house the system's global mode and a local copy
of it so we can keep track of when it changes.  When the global-mode changes,
this will call into the screens multimethod and switch the child content at the
root.  That's all that's needed to have a pretty decent screen-switching
system.

Once we get around to adding playing input, we'll be able to start switching
screens without touching the update function any more.

## Connecting the new root

To see the new root in action, we change the last call in core to this:

```
(procore/start-game game-system
                    [(make-root game-system)])
```

We'll take a quick look at the `global-mode-path` again: notice that the
new `make-root` is at position `0` in the game tree vector and has a
key `:global-mode` assoc'd into it.  This is one way to manage and supply
the global mode in *Prospero*.  (Future versions of the engine may also
support more complicated ways of specifying modes.)

## One Last Touch

Let's make a quick background for our game.

In a background namespace, we'll add:

```
(defn make-background
  [game-system image-path]
  (-> (progo/base-object game-system)
      (progo/bounds-box  1024 768)
      (progo/position-3d 0 0 -1)
      (progo/texture     image-path)))
```

Now let's add it to the top of the children in the `bugs.screens.title` namespace:

```
(background/make-background game-system "images/starfield.png")
```

we  should see the background image show up under our title bugs, now.
Later we'll explore changing the background so it scrolls between levels and
some other effects.

## Recap

We've now created the necessary systems in the game to smoothly handle different
screens and we've set up the groundwork for pause, which can be quite troublesome
in a mostly functional game engine.  We've been able to work with the game tree,
build sprites, and we can now do some rudimentary changes to the tree state with
`update-state`.

## What's Next

This still isn't much of a game.  Our next step will add the most important part:
player input.
