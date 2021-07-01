#!/bin/bash
export PATH="/usr/lib/jvm/java-8-openjdk/jre/bin/:$PATH"
files="core/Player.class core/Country.class core/RiskGame.class Risk.class RiskUtil.class"
out="out/production/Domination/net/yura/domination/engine/"
decomp="src_decomp/net/yura/domination/engine/"
for file in $files
do
	cp "$out$file" "$decomp$file"
done
jar cfm Domination2.jar src_decomp/META-INF/MANIFEST.MF -C src_decomp/ .
