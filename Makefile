# Makefile 

# Copyright (C) 2014 - Saez-Rodriguez Lab
# Cytocopter

# Author: Emanuel Goncalves <emanuel@ebi.ac.uk>

# This program is free software, you can redistribute it and/or
# modify it under the terms of the new-style BSD license.

# You should have received a copy of the BSD license along with this
# program. If not, see <http://www.debian.org/misc/bsd.license>.

NAME=cytocopter
VERSION=2.0.1

default: mvn-build
	cp target/$(NAME)-$(VERSION).jar /Users/emanuel/CytoscapeConfiguration/3/apps/installed/

mvn-build:
	mvn clean install

lsof:
	lsof -i | grep Rserve