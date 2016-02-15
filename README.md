## Sketch Tech
Is a text based rendering engine prototype. Explores some aspects of OOP.
##Problem
##High level solution
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
- **Graphic Shell** executes a graphic command and sends the results to the renderer
- **Canvas** represents the rendering area
- **Renderer** renders the canvas onto specific output, in this case system console.
- 

##Implementation details
Plesae check the diagram

##Execution
Git and Apache Maven is required
Current releaset version is tagged with RELEASE1 tag, please run: git checkout RELEASE1
Execute the application with the following command: mvn



