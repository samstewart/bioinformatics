/**
* Predicts the disease that a patient has based on our training data and the patients
* gene expression data.
* We can think of this as the model layer with jQuery as the controller.
* We subclass LearningImporter since we want to import the exact same type of data but
* with only slightly different labels (training vs testing data).
*/


function Predictor() {
    this.trainingGenes            = null;
    // we need to keep the testing genes separate from the patients
    // due to doing a "deep" type conversion when importing from the database.
    this.testingPatients          = null;
    // raw contents of file we imported
    this.raw_contents             = null;
    // our list of predictive genes (though not indexed by disease)
    this.predictive_genes         = []; 
    this.possible_diseases        = []; // currently should be list of length 2
    
    // total number of predictive genes to use for both classes
    this.PREDICTIVE_GENES         = 50;
    

    // TODO: get the testing data and the training data
    // We should have our own connection from the database?
}

// we are a subclass of learning importer
Predictor.prototype = new LearningImporter();
// rewire annoying constructor
Predictor.prototype.constructor = Predictor;

/** Calculates the n top predictive genes for both classes.*/
Predictor.prototype.calculatePredictiveGenes = function(n) {
    var _this = this;

    // find all negatively predictive genes
    var negativeGenes = this.trainingGenes.filter(function(gene) {
        return (gene.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]) < 0);
    });
    
    var positiveGenes = this.trainingGenes.filter(function(gene) {
        return (gene.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]) >= 0);
    });
    
    // now sort both lists by magnitude
    positiveGenes = positiveGenes.sort(function(a, b) {
        var scoreA = b.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
        var scoreB = a.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
        
        scoreA = Math.abs(scoreA);
        scoreB = Math.abs(scoreB);
        
        return scoreA - scoreB;
    });
    
    negativeGenes = negativeGenes.sort(function(a, b) {
        var scoreA = b.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
        var scoreB = a.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
        
        scoreA = Math.abs(scoreA);
        scoreB = Math.abs(scoreB);
        
        return scoreA - scoreB;
    });
    
    // make sure we have at least enough on both ends
    if (n > negativeGenes.length + positiveGenes.length) {
        return null;
    }
    
    // take top n predictive genes
    this.predictive_genes = [].concat(negativeGenes.slice(0, n/2), positiveGenes.slice(0, n/2));
    
    console.log(positiveGenes.map(function(gene) {
        return gene.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
    }));
    
    console.log(negativeGenes.map(function(gene) {
        return gene.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
    }));
}

/** Loads the data from a learning importer. We still need the training data.*/
Predictor.prototype.loadDataFromTraining = function(trainingImporter) {
    var start = new Date().getTime();
            
    this.possible_diseases = [new Disease('AML', 'testing'), new Disease('ALL', 'testing')];
    
    this.trainingGenes = trainingImporter.gene_set.genes;
    this.calculatePredictiveGenes(Math.floor(this.PREDICTIVE_GENES));
    this.testingPatients = LearningImporter.prototype.patient_set.patients; // because we are an importer we have our own patient set
    console.log('Took ' + (new Date().getTime() - start) / 1000 + 's');
    
}

/** 
* Get voting scores for both classes.
* We use only the subset of predictive genes for each class. We
* then ask each of those genes for their vote about which class
* a patient should belong to based on their underlying gene expressions.
* If the vote is positive, we go with class A. If the vote is negative we go with
* class B. This is equivalent to seeing "which mean" our observed gene expression is closer
* to across all training classes.
* @param patient a new patient or "sample" we want to classify.
* @return a dictionary of class vote scores.
*/
Predictor.prototype.getClassVoteScores = function(patient) {
    var classAVotes = 0;
    var classBVotes = 0;
    
    var _this = this;
    // go through all our predictive genes and have them vote.
    // Stick their votes in one of two sums indexed by disease.
    this.predictive_genes.map(function(gene, index, arry) {
        // we are looking at the *observed* gene's expression level and
        // seeing which class the patient should belong to based on which way
        // that particular gene predicted the patient's disease in the past.
        
        // we have to get the gene from the patient since our set of genes won't know
        // about the training genes.
        var vote   = gene.getVote(patient, _this.possible_diseases);
        
        // now we weight by the class correlation
        var weight = gene.getClassDistinctionCorrelation(_this.possible_diseases[0], _this.possible_diseases[1]);
        
        // decide which "mean" the observed gene is closer to and add the positive
        // value of that score to the appropriate votes
        if (gene.getClosestClass(patient, _this.possible_diseases).name == _this.possible_diseases[0].name) {
            classAVotes += Math.abs(vote * weight);
        } else {
            classBVotes += Math.abs(vote * weight);
        }
        
    });
    
    var votes                             = {};
    votes[this.possible_diseases[0].name] = classAVotes;
    votes[this.possible_diseases[1].name] = classBVotes;
    
    return votes;
};

/** Calculates the prediction strength for a given winning and losing sum of votes*/
Predictor.prototype.calculatePredictionScore = function(v_win, v_lose) {
    return (v_win - v_lose) / (v_win + v_lose);
}

/** 
* Gets the predicted class along with a prediction strength.
* TODO: generalize this to any number of classes?
*/
Predictor.prototype.makePrediction = function(patient) {
    var votes  = this.getClassVoteScores(patient);
    var classA = votes[this.possible_diseases[0].name];
    var classB = votes[this.possible_diseases[1].name];
    
    console.log('Class A votes: ' + classA + ' class B votes: ' + classB);
    
    // TODO: note that we slightly bias the tie breakers in favor of A
    if (classA >= classB) {
        return new Prediction(this.possible_diseases[0], this.calculatePredictionScore(classA, classB), patient, classA, classB);
    } else {
        return new Prediction(this.possible_diseases[1], this.calculatePredictionScore(classB, classA), patient, classA, classB);        
    }
}

/** 
* Returns a list of predictions for given patients. You should have already called 'loadData()'.
* Returns an accuracy score along with a list of predictions.
*/
Predictor.prototype.testPredictor = function() {
    var _this = this;
    var result = {};
    
    result['predictions'] = this.testingPatients.map(function(patient, index, arry) {
       return _this.makePrediction(patient);
    });
    
    var true_diagnosis = this.testingPatients.map(function(patient, index, arry) {
       return patient.disease; // name of the disease 
    });
    
    result['accuracy'] = this.calculateAccuracy(result['predictions'], true_diagnosis);
        
    return result;
};

/** Calculates our prediction accuracy based on our set of predictions and the true diagnoses*/
Predictor.prototype.calculateAccuracy = function(predictions, true_diagnosis) {
    var totalRight = 0;
    predictions.forEach(function(pred, index, arry) {
        if (pred.isUnsure()) {
            return; // count this as wrong
        }
        
        // if we're sure, check if we're right
        // probably just do a cast to be more concise, but then meaning of code might be obscured
        // otherwise see if we're right
        totalRight += (pred.isCorrect() ? 1 : 0);
    });
    
    return totalRight / predictions.length;
}