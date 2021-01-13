# show graph
Translates a particular directed graph data structure (graph with vertices and edges) into a JavaFX view that can
be seen from [Reveal](https://vlaaad.github.io/reveal/).

To see it working in Reveal clone this library and follow these instructions:
- Setup a REPL with aliases dev and example
- Start the REPL then load all the code with `(user/refresh)`
- In your IDE go to the `example.example` namespace (under src/example directory)
- Switch REPL NS to Current File
- Follow the instructions there...

This library gives `:view/graph` (current name) capability to Reveal. In your own application you would depend on this 
library (`cjmurphy/show-graph {:mvn/version "RELEASE"}`) in a development only deps.edn alias and make sure that 
`au.com.seasoft.graph.reveal.view` is required. Then use Reveal as per usual and any data structures that conform to 
`au.com.seasoft.graph.graph/graph?` will show the action `:view/graph` on the context menu. Take care that you bring up 
the context menu when you are on the opening `{` of the graph data structure.

### Data Structure

This data structure will be recognised as a graph:

```
{:1 {:2 {:weight 1} :3 {:weight 2}}
 :2 {:4 {:weight 4}}
 :3 {:4 {:weight 2}}
 :4 {}
}
```
![displayed graph](reveal_with_graph.png?raw=true)

Note that many variations are possible. Here a node/vertex is a keyword, but could just as easily be a string or number.
You can mix and match the types of the nodes as well - for example a few or all of them could be strings. Although
the representation of a node is a circle, long labels (up to 30 characters) can run outside the circle. 

Here `{:weight 1}` is the properties map for the directed edge :1 -> :2, but we could have just put `1` - no map at all.
These 'properties' are just going along for the ride - they don't need to be read when laying out a graph. All that 
Show Graph needs to discover is the edges that run between the vertices. 

Another variation is for the data structure not to be a map but a coll of tuples. So given these last two points this 
is also a valid graph:

```
[[:1 [[:2 1] [:3 2]]]
 [:2 [[:4 4]]]
 [:3 [[:4 2]]]
 [:4 []]
]
```

### Is it useful?

Not sure! But it might help your assessment to know that when focused on a node you can re-activate Reveal. So if
node `:1` had the focus pressing the enter key would activate Reveal on the properties map of node `:1` which
is `{:2 {:weight 1} :3 {:weight 2}}`.    

### Is it significant?

This library (cjmurphy/show-graph) depends on a Java library called cjmurphy/ham which contains an algorithm called
HyperassociativeMap, which determines the placement of the nodes. I don't believe that open source Clojure 
code has been able to do this before. Please let me know if in fact I'm wrong and there are other such algorithms 
easily callable from Clojure, or even written in Clojure.   