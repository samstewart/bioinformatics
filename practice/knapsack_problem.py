# simple program to solve knapsack problem
# to help me gain a deeper intuition about the solution

from pprint import pprint

# represents a simple item
class Item(object):
    """
    Simple model of an item in the knapsack
    """
    
    # the value of this item
    value = 0
    
    # the corresponding cost or weight of this item
    weight = 0
    
    def __init__(self, value, weight):
        self.value = value
        self.weight = weight
        
    def __repr__(self):
        return '(V: %.1f, W: %.1f)' % (self.value, self.weight)
    
class Knapsack(object):
    """
    Simple model of a knapsack
    """
    # number of items that can be stored in this knapsack
    capacity = 0
    
    def __init__(self, capacity):
        self.capacity = capacity
        
# represents the table 
# outputs the optimal items (without replacement, same as Adam's model)
# for filling the knapsack with the given items
def pack_knapsack(knapsack, items):
    """
    Packs the knapsack with the optimal set of objects
    """
    capacity = knapsack.capacity
    
    total_items = len(items)
    
    # the DP we use for constructing an optimal solution
    scores = list()
    
    # fill in first row with zeros
    scores.append([0] * (total_items + 1))
    
    # we make a simple dictionary mapping coordinate pairs to tracebacks
    # the final entry will be (capacity, total_items).
    tracebacks = dict()
    
    # we loop through the possible capacities
    # which we'll represent as rows.
    # we'll need to append 0's to the beginning of each row.
    for cur_capacity in range(1, capacity + 1):
        
        # fill a new row of zeros and start column with zero
        scores.append([0] * (total_items + 1))
        
        for cur_total_items in range(1, total_items + 1):
            current_item = items[cur_total_items - 1]
            
            # now we go through each column for each row
            # and decide if we want to include it or not.
        
            # find the two scores
        
            # the first score is if we happen to include it.
            # we then need to find the best score after subtracting
            # off the weight of this item.
            # we also need to jump "back" an item when reversing history because
            # we want to include each item once.
            # We cap the weight at zero so we don't go over the bounds
            
            # we check to see if we could even include this item
            if current_item.weight > cur_capacity:
                continue
                
            include_score = scores[max(0, cur_capacity - current_item.weight)][cur_total_items - 1] + current_item.value
        
            # we don't include the item
            # We cap the weight at zero so we don't go over the bounds
            dont_include_score = scores[cur_capacity][cur_total_items - 1]
            
            # store that score
            score = scores[cur_capacity][cur_total_items] = max(include_score, dont_include_score)
            
            # now make an entry in the trace back table if we chose
            # to include this item.
            if (score == include_score):
                # then we included it.
                # if include_score == dont_include_score we don't care, either works.
                tracebacks[(cur_capacity, cur_total_items)] = current_item
            
    # now we re-construct the items from the traceback.
    # we need to first find a starting element and can then follow the chain
    # backwards.
    # we are always guaranteed to have an entry in the last row at "full capacity"?
    packed_items = list()
    
    pprint(scores)
    
    cur_capacity = capacity
    i = total_items
    # we need to work from highest element downwards (count down by -1)
    # because we are sampling without replacement we will pick one element (at most)
    # from each column
    while (i > 0):
        
        if (cur_capacity, i) in tracebacks:
            
            # found our starting point, follow the chain backwards
            item = tracebacks[(cur_capacity, i)]
            packed_items.append(item)
            
            # jump backwards by size and number of elements
            cur_capacity -= item.weight
            
        # always jump down one elemtn
        i -= 1

    return packed_items    
    
# simple test harness
knapsack = Knapsack(10)

items = [Item(5, 3), Item(7,4), Item(8, 5)]

best_items = pack_knapsack(knapsack, items)

print "Best items: " + str(best_items)