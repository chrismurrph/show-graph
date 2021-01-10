# show graph
Translates a particular directed graph data structure (graph with vertices and edges) into a JavaFX view that can
be seen from [Reveal](https://vlaaad.github.io/reveal/).

To see it working in Reveal follow these instructions:
- Setup a REPL with aliases dev and example
- Start the REPL then load all the code with `(user/refresh)`
- In your IDE go to the `example.example` namespace (under src/example directory)
- Switch REPL NS to Current File
- Follow the instructions there...

This library gives `:view/graph` (current name) capability to Reveal. In your own application depend on this library 
in a development only deps.edn alias and make sure that `au.com.seasoft.reveal.view` is required. Then use Reveal 
as per usual and any data structures that conform to `au.com.seasoft.graph.graph/graph?` will show the action 
`:view/graph` on the context menu. Take care that you bring up the context menu when you are on the opening `{` 
of the graph data structure.

# Data Structure

This data structure will be recognised as a graph:

```
{:1 {:2 {:weight 1} :3 {:weight 2}}
 :2 {:4 {:weight 4}}
 :3 {:4 {:weight 2}}
 :4 {}
}
```

Note that many variations are possible. Here a node is a keyword, but could just as easily be a string or number.
You can mix and match the types of the nodes as well. Here `{:weight 1}` is the properties for the directed edge
:1 -> :2, but we could have put just `1`.

Another variation that is in the works is for the data structure not to be a map but a collection of tuples. We've
hit a snag (see issues #1) on this that should be resolved soon.  