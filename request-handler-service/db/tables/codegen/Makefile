# ConcepTIS project
# (c) Alex V Eustrop 2009
# see LICENSE at the project's root directory
# $Id$
#

AWK?=awk

usage:
	@echo "Usage: make (all|clean|install|wc)"
all:
	@echo "-- all targets"
	cd TISC; ${MAKE} all
clean:
	@echo "-- cleaning all codegen targets"
	cd TISC; ${MAKE} clean
install:
	@echo "-- installing" 
	cd TISC; ${MAKE} install
wc: all
	@echo "counting code lines"
	find ./ -type f -name "*.sql" -or -name "*.awk" -or \
	-name "Makefile" -or -name "*.tab" -or -name "*.ot" | xargs wc -l
