#!/bin/python

# simple script for using the viterbi algorithm to find CpG islands
import csv
from math import log

NEG_INFINITY = float('-inf')

with open('cpg.hmm', 'r') as csvfile:
	cpgreader = csv.reader(csvfile, delimiter='\t')
	cpg = [row for row in cpgreader]
	symbols = [r[1] for r in cpg]
	letters = [r[0] for r in cpg]
	states = [l + s for l, s in zip(letters, symbols)]
	statesnum = range(0, len(states))
	cpg = [r[2:] for r in cpg]
	
	trans = map(lambda r: map(lambda e: float(e), r), cpg)
	
with open('emissions.hmm', 'r') as csvfile:
	emitreader = csv.reader(csvfile, delimiter='\t')
	e = [row for row in emitreader]
	eletters = [r[0] for r in e]
	emits = [r[1:] for r in e]
	emits = map(lambda r: map(lambda e: float(e), r), emits)
	
with open('seq.test', 'r') as seqs:
	seqs = [r.strip() for r in seqs.readlines()]

def emit_prob(k, s):
	bases = ['A', 'C', 'G', 'T']
	
	return emits[k][bases.index(s)]

def ln(x):
	try:
		return log(x)
	except ValueError:
		return NEG_INFINITY

# returns both the viterbi value and the traceback for a given state and sequence offset along with the previous
# viterbi row
def emit(i, k, viterbi):
	l = [ln(trans[j][k]) + viterbi[j] for j in statesnum];
	
	m = max(l);
	
	v = ln(emit_prob(k, seq[i - 1])) + m;
	
	return (v, l.index(m))
	
# returns both the viterbi value and the traceback for a given state and sequence offset along with the previous
# viterbi row.
# Note: the ordering adam gave us is convenient but in general one needs to compute the viterbi values for the silent
# states only *after* computing all the other viterbi values
def silent(i, k, viterbi):
	l = [ln(trans[j][k]) + viterbi[j] for j in statesnum]
	
	# we need to make a copy to ensure the indicies line up correcty
	# we'd better hope no two cells share the same value!
	lf = l[:] # make a copy
	l.pop(k) # remove ourselves since no cycle silent states
	
	# compute the viterbi value first
	v =  max(lf)
	
	return (v, lf.index(v))
	

# builds up the traceback table for a given sequence
def constructTracebacks(seq):
	# initialize the base case
	prev_vrow = [NEG_INFINITY for s in states]
	prev_vrow[0] = 0
	prev_vrow[-1] = silent(0, len(states) - 1, prev_vrow)[0]

	#print prev_vrow
	cur_vrow = [0 for s in states]

	tracebacks = [[NEG_INFINITY for s in states] for i in range(0, len(seq) + 1)]

	for i in range(1, len(seq) + 1):
		# emitting....
		e = [emit(i, k, prev_vrow) for k in statesnum if symbols[k] != '.']
		# silent (should always be calculated *after* the emitting viterbi values)
		e = [silent(i, k, cur_vrow) if symbols[k] == '.' else e[k] for k in statesnum]
	
		tracebacks[i] = [p for v, p in e]
		cur_vrow = [v for v, p in e]
		
		#print prev_vrow
		#print tracebacks[i]
	
		prev_vrow = cur_vrow

	return (tracebacks, cur_vrow)
	
# simple function to trace back through the possible paths and spit out the intervals
# where we thought we were in a CpG island or not
def findIntervals(traceback, start_row):	
	# start at last row of table and work backwards
	cur_state = (lambda m: start_row.index(m))(max(start_row))

	# build up list of intervals indicating cpg island
	end_cpg = -1

	intervals = []
	emissions = []
	for i in range(len(seq) - 1, -1, -1):
		emissions.append(states[cur_state])
	
		if '+' in states[cur_state] and end_cpg == -1:
			end_cpg = i
		
		if ('-' in states[cur_state] or i == 0) and end_cpg != -1:
			intervals.append((i + 1, end_cpg))
			end_cpg = -1
		
		cur_state = tracebacks[i][cur_state]
	
	intervals.reverse()
	
	return intervals
	
# go through all the sequences and build a traceback table and emit the intervals
for seq in seqs:
	tracebacks, start_row = constructTracebacks(seq) # we need the last viterbi row as the start row to do the traceback
	print findIntervals(tracebacks, start_row)