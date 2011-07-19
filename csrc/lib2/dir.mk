# lib2/dir.mk
# 
# Part of the CCNx distribution.
#
# Copyright (C) 2011 Palo Alto Research Center, Inc.
#
# This work is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License version 2 as published by the
# Free Software Foundation.
# This work is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE.
#

LDLIBS = -L$(CCNLIBDIR) $(MORE_LDLIBS) -lccn -L$(SYNCLIBDIR) -lsync
CCNLIBDIR = ../lib
SYNCLIBDIR = ../sync/
# Override conf.mk or else we don't pick up all the includes
CPREFLAGS = -I../include -I..

INSTALLED_PROGRAMS = 
PROGRAMS = ccnbtreetest
DEBRIS = 

BROKEN_PROGRAMS = 
CSRC = ccn_btree.c ccnbtreetest.c
HSRC = 
SCRIPTSRC = 
 
default: $(PROGRAMS)

all: default $(BROKEN_PROGRAMS)

$(PROGRAMS): $(CCNLIBDIR)/libccn.a

LIB2_OBJ = ccn_btree.o
ccnbtreetest: $(LIB2_OBJ) ccnbtreetest.c
	$(CC) $(CFLAGS) -o $@ ccnbtreetest.c $(LIB2_OBJ) $(LDLIBS) $(OPENSSL_LIBS) -lcrypto

clean:
	rm -f *.o *.a $(PROGRAMS) $(BROKEN_PROGRAMS) depend
	rm -rf *.dSYM *.gcov *.gcda *.gcno $(DEBRIS)

check test: ccnbtreetest $(SCRIPTSRC)
	./ccnbtreetest
	: ---------------------- :
	:  lib2 unit tests pass  :
	: ---------------------- :

###############################
# Dependencies below here are checked by depend target
# but must be updated manually.
###############################
ccn_btree.o: ccn_btree.c ../ccn/btree.h
ccnbtreetest.o: ccnbtreetest.c ../ccn/btree.h
