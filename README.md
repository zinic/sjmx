#Smart JMX#

###What is it?###

Smart JMX is a JMX command line utilitiy that aims to be a configurable JMX proxy that's
extensible via Jython MBean filter scripts.

###Um alright... how do I use it?###

mvn clean install

java -jar ./target/sjmx.jar


##The Features##

* Track multiple remote locations and allow switching between them.
* Introspect remote JMX properties.
* Allow extension through Jython scripts.
* Builds Dynamic MBeans that reflect a filtered or otherwise processed model dictated by Jython plugis.
* Hosts built Dynamic MBeans on its own MBeanServer that can be accessed by external tools (like Jolokia).

See [a python JMX filter example](https://github.com/zinic/sjmx/blob/master/PythonFilterExample.py).

###TODO###

* Connect proxy mbeans to all remote locations.


##That Legal Thing...##

This software library is realease to you under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [LICENSE](https://github.com/zinic/sjmx/blob/master/LICENSE) for more information.
