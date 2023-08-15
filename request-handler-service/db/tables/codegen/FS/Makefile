# ConcepTIS project/QTIS projects
# (c) Alex V Eustrop 2009
# (c) Alex V Eustrop & EustroSoft.org 2023
# see LICENSE.ConcepTIS at the project's root directory
# $Id$
#
# FS subsystem tab files:
#  File.ot	- object
#   FFile.tab	- header
#    FDir.tab	- content of directory, if so
#    FBlob.tab	- chunks of binary data
#    FText.tab	- fragments of textual content

AWK?=awk
INSTAB?=NOTHINGTOINSTALL
INSTOT?=NOTHINGTOINSTALL
INSTCODE?=NOTHINGTOINSTALL
CODEGENTOOL=${AWK} -F '\t' -f ../tools/codegen.awk
CODEGENTOOL_OT=${AWK} -F '\t' -f ../tools/codegen_ot.awk
CODEGENTOOL_SUBSYS=${AWK} -F '\t' -f ../tools/codegen_subsys.awk
INSTALL_PATH?=../../FS/
INSTALL_PATH_ZDAO?=../../../../java/zDAO/ru/mave/ConcepTIS/dao/FS/
INSTALL=install -m 644

usage:
	@echo "Usage: make (all|clean|install|install_zdao)"
	@echo "   or: make install_tab INSTAB=RecordName INSTCODE=RecordCode"
	@echo "   or: make install_ot INSTOT=ObjectTypeName INSTCODE=ObjectTypeCode"
	@echo "   or: make install_zdao_class INSTCLASS=NameOfRecordOrObjectType"
all:
	@mkdir -p work/tables
	@mkdir -p work/tables.drop
	@mkdir -p work/views
	@mkdir -p work/views.grant
	@mkdir -p work/views.drop
	@mkdir -p work/sequences
	@mkdir -p work/declarations
	@mkdir -p work/functions
	@mkdir -p work/functions.grant
	@mkdir -p work/functions.drop
	@mkdir -p work/java/zDAO
	@mkdir -p work/java/semiproducts
	@echo "-- all tables"
	cat FFile.tab | ${CODEGENTOOL}
	cat FDir.tab | ${CODEGENTOOL}
	cat FBlob.tab | ${CODEGENTOOL}
	cat FText.tab | ${CODEGENTOOL}
	@echo "-- object types"
	cat File.ot | ${CODEGENTOOL_OT}
	@echo "-- subsystem functions"
	cat File.ot | ${CODEGENTOOL_SUBSYS}
clean:
	@echo "-- cleaning all codegen/FS targets"
	@rm -rf work/tables
	@rm -rf work/tables.drop
	@rm -rf work/views
	@rm -rf work/views.grant
	@rm -rf work/views.drop
	@rm -rf work/sequences
	@rm -rf work/declarations
	@rm -rf work/functions
	@rm -rf work/functions.grant
	@rm -rf work/functions.drop
	@rm -rf work/java
install:
	@echo --
	@echo -- object records
	@echo --
	${MAKE} install_tab INSTAB=FFile INSTCODE=FF
	${MAKE} install_tab INSTAB=FDir INSTCODE=FD
	${MAKE} install_tab INSTAB=FBlob INSTCODE=FB
	${MAKE} install_tab INSTAB=FText INSTCODE=FT
	@echo --
	@echo -- object types
	@echo --
	${MAKE} install_ot INSTOT=File INSTCODE=F
	@echo --
	@echo -- subsystem glue funtions
	@echo --
	${INSTALL} work/functions/commit_object.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/delete_object.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/move_object.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/rollback_object.sql ${INSTALL_PATH}/functions/
install_zdao:
	@echo --
	@echo -- object records
	@echo --
	${MAKE} install_zdao_class INSTCLASS=FFile INSTCODE=FF
	${MAKE} install_zdao_class INSTCLASS=FDir INSTCODE=FD
	${MAKE} install_zdao_class INSTCLASS=FBlob INSTCODE=FB
	${MAKE} install_zdao_class INSTCLASS=FText INSTCODE=FT
	@echo --
	@echo -- object types
	@echo --
	${MAKE} install_zdao_class INSTCLASS=File INSTCODE=F
install_tab:
	@echo Installing INSTAB=${INSTAB} INSTCODE=${INSTCODE}...
	test ! ${INSTAB}x = NOTHINGTOINSTALLx
	test ! ${INSTCODE}x = NOTHINGTOINSTALLx
	test -f ${INSTAB}.tab
	# tables, views, sequences
	${INSTALL} work/tables/${INSTAB}.sql ${INSTALL_PATH}/tables/
	${INSTALL} work/tables.drop/${INSTAB}.sql ${INSTALL_PATH}/tables.drop/
	${INSTALL} work/views/V_${INSTAB}.sql ${INSTALL_PATH}/views/
	${INSTALL} work/views.grant/V_${INSTAB}.sql ${INSTALL_PATH}/views.grant/
	${INSTALL} work/views.drop/V_${INSTAB}.sql ${INSTALL_PATH}/views.drop/
	#${INSTALL} work/sequences/${INSTAB}_seq.sql ${INSTALL_PATH}/sequences/
	# functions for external use
	${INSTALL} work/functions/create_${INSTAB}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/delete_${INSTAB}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/update_${INSTAB}.sql ${INSTALL_PATH}/functions/
	# functions for internal use
	${INSTALL} work/functions/QC_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/rdelete_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/cmp_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/commit_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/rollback_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/move_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/delete_object_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	# drop functions
	${INSTALL} work/functions.drop/${INSTAB}.sql ${INSTALL_PATH}/functions.drop/
	${INSTALL} work/functions.grant/${INSTAB}.sql ${INSTALL_PATH}/functions.grant/

	${MAKE} install_zdao_class INSTCLASS=${INSTAB}
install_ot:
	@echo Installing INSTOT=${INSTOT} INSTCODE=${INSTCODE}...
	test ! ${INSTOT}x = NOTHINGTOINSTALLx
	test ! ${INSTCODE}x = NOTHINGTOINSTALLx
	test -f ${INSTOT}.ot
	# functions
	${INSTALL} work/functions/commit_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/rollback_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/move_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${INSTALL} work/functions/delete_${INSTCODE}.sql ${INSTALL_PATH}/functions/
	${MAKE} install_zdao_class INSTCLASS=${INSTOT}
install_zdao_class:
	${INSTALL} work/java/zDAO/${INSTCLASS}.java ${INSTALL_PATH_ZDAO}

wc: all
	@wc -l work/*/*sql
