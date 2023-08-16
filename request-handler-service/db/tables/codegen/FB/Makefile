# ConcepTIS project/QTIS projects
# (c) Alex V Eustrop 2009
# (c) Alex V Eustrop & EustroSoft.org 2023
# see LICENSE.ConcepTIS at the project's root directory
# $Id$
#
# FS subsystem tab files:
#  Feedback.ot	- object
#   BChat.tab	- Chat
#   FMessage.tab	- Message

AWK?=awk
INSTAB?=NOTHINGTOINSTALL
INSTOT?=NOTHINGTOINSTALL
INSTCODE?=NOTHINGTOINSTALL
CODEGENTOOL=${AWK} -F '\t' -f ../tools/codegen.awk
CODEGENTOOL_OT=${AWK} -F '\t' -f ../tools/codegen_ot.awk
CODEGENTOOL_SUBSYS=${AWK} -F '\t' -f ../tools/codegen_subsys.awk
INSTALL_PATH?=../../FB/
INSTALL_PATH_ZDAO?=../../../../java/zDAO/ru/mave/ConcepTIS/dao/FB/
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
	cat BChat.tab | ${CODEGENTOOL}
	cat BMessage.tab | ${CODEGENTOOL}
	@echo "-- object types"
	cat Feedback.ot | ${CODEGENTOOL_OT}
	@echo "-- subsystem functions"
	cat Feedback.ot | ${CODEGENTOOL_SUBSYS}
clean:
	@echo "-- cleaning all codegen/FB targets"
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
	${MAKE} install_tab INSTAB=BMessage INSTCODE=BM
	${MAKE} install_tab INSTAB=BChat INSTCODE=BC
	@echo --
	@echo -- object types
	@echo --
	${MAKE} install_ot INSTOT=Feedback INSTCODE=B
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
	${MAKE} install_zdao_class INSTCLASS=BChat INSTCODE=BC
	${MAKE} install_zdao_class INSTCLASS=FMessage INSTCODE=BM
	@echo --
	@echo -- object types
	@echo --
	${MAKE} install_zdao_class INSTCLASS=Feedback INSTCODE=B
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
