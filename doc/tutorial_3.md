# Making a Game with Prospero and Iris

# Tutorial 3: Player Input

## What we'll Learn here

So far we've built a couple screens, but nothing you would call a "game".  Without
player input, we don't really have a game.  This tutorial will demonstrate how
to handle player input, getting us closer to something that is actually playable.

## Handling an Event on the title screen

Let's start with a simple bit of input handling to transition off the title screen
and move to the game screen.

Create a new namespace `bugs.screens.game` for where the action will take place:

```
(ns bugs.screens.game
  (:require [bugs.screens :as screen]
            [bugs.background :as background]))

(defmethod screen/screen-children :game-screen
  [code game-system]
  [(background/make-background game-system "images/starfield.png")])
```

Add this and the events namespace to the `:require` expression in `core` like so:

```
[bugs.screens.game]
[prospero.events :as proevent]
```

Now we have a screen we'll change to and the event support we need.

Add a call to `progo/process-event` in the builder `make-root` in the core
namespace. To this, we'll add an event map that is responsible for changing
state and dispatching events based on player input.

Inside this call, we'll create an event map like so:

```
{[::proevent/keyboard-up ::proevent/key-any-key {:active-global-modes #{:title-screen}}]
 #(assoc % :global-mode :game-screen)}
```

Let's break this down.  The map's key is a vector describing the event.  The
first part is the type of event `::proevent/keyboard-up`.  This is great for any
quick key-press. The second part is the actual key for that input type, in this
case, `key-any-key` so that any key-press will take us out of the title screen
and into the game.  In `prospero.events`, you can see all of the defined event
codes listed for your reference.  The final argument in the event is for options.
In this case, we have used `:active-global-modes` to constrain this event to the
`:title-screen` global mode only.

The value for this event is a function that is applied to the game state.  Here,
we just switch the global mode to `:game-screen` triggering the update children
mechanism we built in the last tutorial to switch to a plain background screen
that doesn't have the bugs title text.  Now we will add the player.

## Adding a Player Game Object and its Movement

Let's add a simple player namespace like so:

```
(ns bugs.player
  (:require [prospero.game-objects :as progo]))

(def tile-size 64)

(defn make-player
  [game-system x-loc y-loc]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   tile-size tile-size)
      (progo/sprite-sheet {:sprite-sheet-path "/images/sprite_sheet.png"
                           :sprite-width      tile-size
                           :sprite-height     tile-size
                           :columns           5
                           :rows              5})
      (progo/sprite-index 0 1)
      (progo/position-3d  x-loc y-loc 0)))
```

This is pretty much a direct copy of the code from the `enemies` namespace,
but we've adjusted it slightly to provide the base for future expansion
of the player game object and our game's player domain model.

We make the player object visible in the game screen by adding
`(player/make-player game-system 492 600)` to the `screen-children`
multimethod in `bugs.screens.game`.  Figwheel will do its thing, and
the player tank should show up in the game screen.

Now we can wire up events to move the player model.  We'll start by adding some
input events to the `bugs.core` namespace.

First, let's add `[bugs.player :as player]` to the `core` namespace
`:require`.  That will let us use namespaced keywords for some
signals.  It's nice to use namespaced keywords to organize events in large
games.

Next let's add a key-value pair like this to the `process-event` map:


```
  [::proevent/keyboard-held ::proevent/key-arrow-left {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-left]
```

This maps the left arrow key to a signal: `::player/move-left`.  Signals are
just another *Prospero* event type that we can use to freely create events in
our games "model".  We can use signals to map multiple physical keys or clicks
to one in-game action.  This example also demonstrates one of *Prospero's*
available built-in event handlers.  This are supplied as vectors with a
namespaced keyword for the dispatch value.

The remainder of the events are hooked up like so:

```
  [::proevent/keyboard-held ::proevent/key-letter-a {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-left]

  [::proevent/keyboard-held ::proevent/key-arrow-down {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-down]

  [::proevent/keyboard-held ::proevent/key-letter-s {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-down]

  [::proevent/keyboard-held ::proevent/key-letter-d {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-right]

  [::proevent/keyboard-held ::proevent/key-arrow-right {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-right]

  [::proevent/keyboard-held ::proevent/key-arrow-up {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-up]

  [::proevent/keyboard-held ::proevent/key-letter-w {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-up]
```

If you want to test the events, feel free to add the following block to the
player game object and try out some rudimentary movement.

```
(progo/process-event
  {[::proevent/signal ::move-left]
   #(update-in % [:translation 0] dec )

   [::proevent/signal ::move-down]
   #(update-in % [:translation 1] inc )

   [::proevent/signal ::move-right]
   #(update-in % [:translation 0] inc )

   [::proevent/signal ::move-up]
   #(update-in % [:translation 1] dec )})
```

Again, this uses functions which are fine for custom or complicated updates,
but we'll use something a bit better in a moment.

Better, in this case, is animators.  *Prospero* includes a limited facility for
declarative specification of animations.  That's an area that's going to get a
lot of attention in future versions.  Animations may be pulled out to their own
library which might allow the animation system to be completed plugable, too.
For now, let's use `progo/add-animators` to supply a vector of animators to
the player game object:

```
(progo/add-animators
       [[[:translation 0] [[20 :pixels] [1 :second]] :constant {:limit         1000
                                                                :animation-id  ::anim-right
                                                                :initial-state :stopped}]
        [[:translation 0] [[-20 :pixels] [1 :second]] :constant {:limit 20
                                                                 :animation-id  ::anim-left
                                                                 :initial-state :stopped}]
        [[:translation 1] [[-20 :pixels] [1 :second]] :constant {:limit         20
                                                                 :animation-id  ::anim-up
                                                                 :initial-state :stopped}]

        [[:translation 1] [[20 :pixels] [1 :second]] :constant {:limit         20
                                                                :animation-id  ::anim-down
                                                                :initial-state :stopped}]])
```

If we look at an individual animation specification in detail:

```
[[:translation 0] [[20 :pixels] [1 :second]] :constant {:limit         1000
                                                        :animation-id  ::anim-right
                                                        :initial-state :stopped}]
```

The first part is a path into the game object to find the value we're going to modify.
The second part is a rate of change.  The third piece is currently a mostly-ignored
filler parameter.  The options at the end are used to identify the animation
and control where it stops.  (Again, there are plans to refine this language
quite a bit from where it currently stands.)

To trigger the animation system, we adapt the player game object's
`process-event` map to have entries like these:

```
  [::proevent/signal ::move-left]
  [::proevent/change-animators-on-event {::anim-left  :running
                                         ::anim-right :stopped
                                         ::anim-up    :stopped
                                         ::anim-down  :stopped}]
```

This is another built-in from the event system that updates the state of running
animators.

To provide an old-school feel where the player can only move in one of the
four cardinal directions at any time, we add `keyboard-up` signals like so:

```
  [::proevent/keyboard-up ::proevent/key-letter-a {:active-global-modes #{:game-screen}}]
  [::proevent/emit-signal-on-event ::player/move-stop]
```

To trigger an event handler like this:

```
[::proevent/signal ::move-stop]
        [::proevent/change-animators-on-event {::anim-left  :stopped
                                               ::anim-right :stopped
                                               ::anim-up    :stopped
                                               ::anim-down  :stopped}]
```

Now whenever a key is lifted movement will stop in all directions, providing
that classic arcade 4-way stick feel.

## One Last Touch

Now let's add one more little flourish.  Let's animate the player a bit while it
moves.

Add an animator like this to the player's vector of animators:

```
  [[:texture :col] [[1 :unit] [300 :ms]] :constant {:limit         3
                                                    :domain        :integers
                                                    :on-limit      :reset
                                                    :initial-value 0
                                                    :animator-id   ::anim-move
                                                    :initial-state :stopped}]
```

There are a few new pieces here.  Later, try removing or changing those new
keys to see what happens.

Update the `::proevent/change-animators-on-event` instructions to include
`::anim-move  :running` and see what happens.

## Recap

In this tutorial, we covered how to take in player input from the keyboard and
how to direct that input to signals that are more domain-specific events.
We also learned how to specify animations, trigger them via events, and build
basic player movement.

## What's Next

Now that the player has some degree of agency, we'll set the stage for the game's
challenge in the next tutorial.  There, we will add enemies by using events to
modify the child lists of game objects.
