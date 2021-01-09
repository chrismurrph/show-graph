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

I went with the chess example in the end. Straightforward once I understood that the label has to go last so that
focus can be seen as tab through. I still count the membrane way as the ideal - and will go for it if get into 
trouble. 

I would like the user to click on a node to get the focus, but Reveal owns the handler. Will have to ask Vlaaad about
this. Maybe use membrane to get there... 

Could follow phronmophobic's tree-map's example at some point later on: alter so don't need to supply a `graph?` but 
rather an `obj` and a set of functions for navigating the graph. Thus this library will be able to accept any definition 
of a graph. Trouble with this is the user (or someone) has to write functions. I prefer the ideal of having a widely 
accepted canonical but lax definition of a graph. And to translate to this structure from other structures. An obvious one to 
try is Fulcro state - but that would be a huge graph. Interestingly exploring Fulcro state from root down, allowing user
to traverse one edge at a time, is actually tree exploration. So better to display the whole graph. Maybe we could filter
out the to-many nodes. But then for the graph to be connected we would want to filter out their lookups as well. So 
filter out the edge lookups found by a topological sort?? Getting complicated... Hmm - whole thing might always be
connected if every node comes off the root. Then the nodes only used by the root can be removed. To get 'only used'
reverse the graph.       

