# Making a Game with Prospero and Iris

# Tutorial 0: Initial Setup

## What we'll Learn here

This section is going to be fairly light on explanation.  If you're experienced
with Clojure at all, this should all seem like fairly common starting tasks
so not a lot of explanation is required.

If you're new to Clojure, please just follow along.  These steps are what you
need to get a project running, but explaining them is well outside the scope
of this tutorial.  Once we're over the initial steps, there will be more
explanation and most of this stuff will become second-nature very quickly.

Just so it feels like some progress is made, once we have the project set up,
we will have *Prospero* display our first game object.

## Install Leiningen and create a new project

Start by installing [leiningen](https://leiningen.org/), if you do not have it
on your system.  This is a project system that should make sure the environment
the tutorial calls for is reasonably consistent.

Hopefully you're familiar with Clojure and leiningen already, although we should
be able to get up and running without too much advanced knowledge.

Once leiningen is available, start a new console/terminal and type the following:

```
lein new figwheel bugs
```

This will create a folder for your project and set up the basic environment


There's one more bit of setup the *Figwheel* template will require:
```
cd bugs
npm install
```

Hopefully you have npm installed.  *Figwheel* is a tool that will reload our code
in the browser as we make edits.  It's really useful for rapid development,
but this template requires that call in to npm.  That's probably the last we'll
have to worry about it, though.


## Testing Figwheel

Type in your bugs folder:

```
lein figwheel
```

After a little bit, you should see your browser open on
[http://localhost:3449](http://localhost:3449).


Now open `src/bugs/core.cljs` with your editor and add the following to the file:

```
(prn "can you see this?")
```

If figwheel is configured correctly, `can you see this?` should appear in your
browser console.  From here, we have one more small setup task and we can
start writing our game.


## Add Prospero to your project

Open `project.clj` in the root of your project folder and edit the
`:dependencies` key, so it looks like this:

```
:dependencies [[org.clojure/clojure       "1.10.0"]
               [org.clojure/clojurescript "1.10.773"]
               [prospero/prospero         "0.1.0"]
               [prospero/iris             "0.1.0"]]
```

Exit figwheel and restart it:

```
lein figwheel
```

You should see leiningen fetch these dependencies and start figwheel again.

## First Rendering with Prospero

Now that everything is started, let's actually try rendering something:

Open `src/bugs/core.cljs` for edit:

Remove everything in the file and start with an `ns` form like this:

```
(ns bugs.core
  (:require [iris.core :as iris]
            [prospero.core :as procore]
            [prospero.game-objects :as progo]))
```

`[iris.core :as iris]` pulls in the dependencies *Prospero* needs to use
*Iris* as its rendering engine.

`[prospero.core :as procore]` is required to pull in the `start-game` function.

`[prospero.game-objects :as progo]` is where we'll spend most of our time.  This
namespace is a collection of functions that are used to build objects (or nodes)
in our game state.

Follow the ns declaration with a `def` for our game system configuration:

```
(def game-system {::procore/game-system ::iris/game-system
                  :display-width        1024
                  :display-height       768
                  ::iris/web-root       (.getElementById js/document "app")})
```

`::procore/game-system ::iris/game-system` is what tells *Prospero* to use *Iris*.
`::iris/web-root` is a key specific to *Iris* that accepts the root DOM element that will
be replaced with the game.

`(.getElementById js/document "app")` is just a ClojureScripty way to use the browser's
DOM API.  `"app"` is the id of an element provided by the *Figwheel* template  (You can
find it in `resource/public/index.html`).


With the game system defined, we can actually start a game.  Type the following and save the file:

```
(procore/start-game game-system [])
```

When the file is saved, *Figwheel* should update the page and you'll see a blank white page with
no content.  *Prospero* and *Iris* have replaced the `"app"` element with the game, but it's a
blank white square, since we've provided an empty vector, `[]`, or game objects.

Now, replace that line with the following and save:

```
(procore/start-game game-system
                    [(-> (progo/base-object game-system)
                         (progo/bounds-box  1024 768)
                         (progo/colour-rgb  255 0 0))])
```

This should create a large, red box which is our finishing point for this leg of the tutorial.

This form uses the functions in `prospero.game-objects` to build up a game object.

`(progo/base-object game-system)` is the starting point for all game objects.

`(progo/bounds-box 1024 768)` says to make the object 1024 pixels wide by 768 pixels tall.
You can experiment with this by editing the values and saving the file.

`(progo/colour-rgb 255 0 0)` gives us a pure red box.  The 3 parameters are red, green,
and blue and can range from 0 - 255.  Experiment with those values too to get a sense
of how this works.

## What's Next
We've seen everything you need to get started.  Our next step will build the game's core
state management and a moving enemy bug.
