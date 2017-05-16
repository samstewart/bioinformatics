/** simple class that represents a single gene*/

function Gene(name, learning_type) {
    this.type               = 'Gene';
    this.learning_type      = learning_type; // learning or testing data set?
    this.name               = name;
    this.PATIENT_KEY_PREFIX = 'Patient Key ';
    // list of "cleaned" patients (no gene fields to avoid circular references) that I am expressed in
    this.patients_expressed_in = [];
    // array of numeric expression levels indexed by index of patient
    this.expression_levels     = [];
    
    // we want to cache the std and the mean once we compute them to speed up calculations
    this.expression_mean       = {};
    this.expression_std        = {};
}

Gene.prototype.setExpressionLevel = function(patient, expression_level) {
    // if we haven't already seen this patient, add it
    // we make a copy for ourselves but remove the gene property to avoid circular references.
    // After all, we really only need the p_index for filtering by disease since wee
    // maintain our own list of expression levels.
    var cleaned_patient = new Patient(patient.p_index, patient.disease, patient.learning_type);
    cleaned_patient.genes = {};
    
    // the p-indices are not zero, but one, indexed.
    this.patients_expressed_in[patient.p_index - 1] = cleaned_patient;
    
    this.expression_levels[patient.p_index - 1] = expression_level;
}

Gene.prototype.getExpressionLevel = function(patient) {
    return this.expression_levels[patient.p_index - 1];
}

/** Helper method to determine whether we are closer to class A or class B mean*/
Gene.prototype.getClosestClass = function(patient, possible_diseases) {
    var uA = this.getExpressionMean(possible_diseases[0]);
    var uB = this.getExpressionMean(possible_diseases[1]);
    
    // observed expression level of ourselves
    var observed = patient.getGeneExpressionByName(this.name);
    
    var dist1 = Math.abs(observed - uA);
    var dist2 = Math.abs(observed - uB);
    
    if (dist1 < dist2) {
        return possible_diseases[0];
    } else {
        return possible_diseases[1];
    }
}
/** 
* Gets this Gene's *weighted* vote. Assumes binary class distinctions.
* It looks at the observed level of ourselves in a new sample.
* We return a score which might be negative but we want it this
* way so we can tell which class to count it towards.
* We extract our "observed" value from the new sample (patient)'s set of observed
* genes.
*/
Gene.prototype.getVote = function(patient, possible_diseases) {
    var _this = this;
    
    // since we only have two classes this will be uA and uB
    var sumOfMeans = _this.getExpressionMean(possible_diseases[0]) + _this.getExpressionMean(possible_diseases[1]);
    
    // return vote for observed value of this gene
    return (patient.getGeneExpressionByName(this.name) - 1.0 / 2.0 * sumOfMeans);
}
/** Gets the expression level std for the given disease
* TODO: should this be done through the database with a map/reduce
*/
Gene.prototype.getExpressionStd = function(disease) {
    if (this.expression_std[disease.name] != undefined) {
        return this.expression_std[disease.name];
    }
    // get the patients with this disease that we are expressed in.
    // Think of vertical cut down through data.
    var expressed_in_disease = this.patients_expressed_in.filter(function(patient) {
       return patient.disease == disease.name;  
    });
    
    // sum up the expression levels and divide to get sample mean
    var mean = this.getExpressionMean(disease);
    
    // we keep a running total as we fold from left to right.
    // we add our current squared deviation to the partial sum and continue.
    // We need an initial value of zero since we need to 'seed' the calculation
    // with the original squared deviation.
    // TODO: should we be using sample mean (divided by number of cases?) or population
    
    // how many times were we expressed (how many samples)
    var numCases = expressed_in_disease.length;
    
    var _this = this;
    
    // calculate sum of squared deviations across all expressed patients and divide
    // by n - 1 since sample standard deviation.
    var sd  = Math.sqrt(
                    expressed_in_disease.reduce(
        
                        function(l, r, index, array) {
                            // accumulate the squared deviations
                            return l + Math.pow((_this.expression_levels[r.p_index - 1] - mean), 2); // compute squared deviation and role into sum
                        }, 
                    0) / (numCases - 1)
                    
                    );
                    
    this.expression_std[disease.name] = sd;
    
    // you have to divide by DF since sample standard deviation;
    return sd; 
    
}

/** Gets the expression level mean for the given disease
* TODO: should this be done through the database with a map/reduce
*/
Gene.prototype.getExpressionMean = function(disease) {
    if (this.expression_mean[disease.name] != undefined) {
        return this.expression_mean[disease.name];
    }
    
    // get the patients with this disease that we are expressed in
    // in other words, we are getting all expression levels for a given class.
    var expressed_in_disease = this.patients_expressed_in.filter(function(patient) {
       return patient.disease == disease.name;  
    });
    
    // how many times were we expressed (how many samples)
    var numCases = expressed_in_disease.length;
    
    // sum up the expression levels and divide to get sample mean
    // we map over the patients with the specified disease that we were expressed in
    // and then sum up the expression values by doing a lookup
    var _this = this;
    var mean = expressed_in_disease.reduce(function(l, r, index, array) {
         return l + _this.expression_levels[r.p_index - 1];
    }, 0) / numCases; 
    
    this.expression_mean[disease.name] = mean;
    
    return mean;
}

/** 
* Computes the specialized correlation coefficient for difference in classes.
* TODO: generalize to multiple classes?
*/
Gene.prototype.getClassDistinctionCorrelation = function(disease1, disease2) {
    // this equation comes from the author's special correlation measure.
    // Positive values indicate highh expression in class 1 and negative values indicate high expression in class 2
    // Large (magnitude) values of this ratio represent high correlation with an idealized class vector c = (1,1,1...,0,0,0...)
    // Because the STD is small and the gap between the two expression levels in both classes is large.
    return (this.getExpressionMean(disease1) - this.getExpressionMean(disease2)) / (this.getExpressionStd(disease1) + this.getExpressionStd(disease2));
}