# Sketch Tech
Is a text based rendering engine prototype. Explores some aspects of OOP.
#Problem
#High level solution
The diagram below ilustrates the main components of the application and their interactions
<pre>
Available Cmmands

+----------------------+     +---------------------------------+
| Line Command         |     |                                 |
+----------------------+     | +----------------------------+  |
+----------------------+     | |                            |  |
|Rectangle Command     |     | | +--------+   +----------+  |  |        +---------------+
+----------------------+     | | | canvas +---> Renderer +-------------->System Console |
+----------------------+     | | +--------+   +----------+  |  |        +-------+-------+
|Bucket Fill Command   |     | |                            |  |                |
+----------------------+     | | Graphic Shell              |  |                |
|Create Canvas Command |     | +-^---------^----------------+  |                |
+----------------------+     |   |         |                   |                |
                             |   |         |                   |          +-----v-------+
                             |   |Exec Cmd |New Canvas         <----------+ Sketch REPL |
                             |   +         +                   |          +-------------+
                             |  Sketch Command Processor       |
                             +---------------------------------+
<pre>
- Sketch REPL : has the role of Read Evaluate Print Loop component that reads the literal commands from the System Console and passes them for execution to the Sketch **Sketch Command Processor** component. The REPL will terminate the loop when **Exit Command** is received.
- **Sketch Command Processor** parses validates and if valid pases the command for execution to the **Graphic Shell** component.
- 



