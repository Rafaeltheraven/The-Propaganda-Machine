#!/bin/sh
cd "`dirname "$0"`"
java -cp Domination2.jar net.yura.domination.ui.commandline.CommandText "$@"
