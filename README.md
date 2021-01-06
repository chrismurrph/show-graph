# show graph
Translates a particular graph data structure (graph with vertices and edges) into a JavaFX view that can
be seen from [Reveal](https://vlaaad.github.io/reveal/).

To see it working in Reveal follow these instructions:
- Setup a REPL with aliases dev and example
- Start the REPL then load all the code with `(user/refresh)`
- In your IDE go to the `example.example` namespace (under src/example directory)
- Switch REPL NS to Current File
- Follow the instructions there...

To give this `:view/graph` capability to your own application depend on this library in a development only
deps.edn alias and make sure that `au.com.seasoft.reveal.view` is required. Then use Reveal as per usual 
and any data structures that conforms to `au.com.seasoft.graph.graph/graph?` will show the action `:view/graph` 
on the context menu. However make sure you bring up the context menu when you are on the opening `{` of the 
graph data structure.  

# My notes

Am stuck at the tooltip part. With the tooltips example it seems that there has to be props - so the atom is
where the points go, and they get fed out. We could I suppose get this working in stand alone example, so
actually see the popup. But what way of displaying does Reveal use? The setup of the scene is unknown to me and
yet I'll be relying on fx/mount-renderer. Does Reveal use `fx/mount-renderer` or one of the other ways (for instance
the re-frame way)? 

Question answered, does indeed use `fx/mount-renderer` when `vlaaad.reveal.ui/make` is called, which it is no matter
how start Reveal, and we start Reveal using ui. So next question is how to get at the Reveal atom dynamically? We
are going to have to put our points/coords into that atom at the time the `:view/graph` action is activated from 
the context menu. We should test that this can be done before bothering to see the tooltip independently of Reveal.

Even when both things done, will it work? That's a question to pose to vlaaad...

Before any of this lets get the chess board working. Notes at end of Reveal README show exactly how - need to bring
the context menu up on the Reveal atom (the same thing we need to alter). 

Reveal has `*state` that has `:views`. Is the chess info put into one of these keys? So we need to put into and fetch
from our own `:graph` key. Does this key already exist?  

So phronmophobic has just done a tree-map Reveal view using his membrane library. Thus we can exchange the above
problems for an altered set and learn membrane at the same time. tree-map uses fx state properly so we will also be
learning fx. So if take this avenue won't need to explore the chess example. Trouble here is would have to re-do 
what already done in fx in membrane.

Following tree-map's example at some point later on alter so don't need to supply a `graph?` but rather an `obj` and
a set of functions for navigating the graph. Thus this library will be able to accept any definition of a graph.    

