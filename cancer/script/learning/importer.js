/**
* Code that imports the training data to the database from an uploaded file.
* We simply stick the code in the database.
* We can get our own handle to the database, thank you very much!
*/

LearningImporter.prototype.patient_set = null; // so that children classes can access them

function LearningImporter() {
    this.raw_contents   = null;
    this.patient_set    = null;
    // mapping from disease names to diseases
    this.diseases       = {};
    // names of disease names.
    // TODO: make this learnable from actual data
    this.DISEASE_NAMES = ['AML', 'ALL'];
}

/** 
* Imports and parses the Gene section. 
* @param learning_type either 'training' or 'testing'
*/
LearningImporter.prototype.importFile = function(learning_type, callback) {
    // list of patients we've seen
    LearningImporter.prototype.patient_set        = new PatientSet(learning_type);
    // long list of genes (ordering is important to ensure consistent indices)
    this.gene_set           = new GeneSet(learning_type);
    
    console.log('Importing file...');
    
    // setup the gene names ahead of time
    var _this = this;
    this.DISEASE_NAMES.forEach(function(name) {
        _this.diseases[name] = new Disease(name, 'learning');
    });
    
    // parse the different components of the file
    var lines = this.raw_contents.split('\n');
    
    // import the gene *names*
    this.importGeneNames(lines[0], learning_type);
    
    // now loop through the remainer of the file and import the patients and their gene expression levels
    lines = lines.slice(1);
    
    console.log('Parsing ' + lines.length + ' patients (samples)');   
    // convert each to a patient
    lines.forEach(function(element) {
        this.importPatient(element, learning_type);
    }, this);
  
    callback(100);
  // saving stuff to the database turns out to be much more hassle than its worth.
  // In the future we might try to wrestle with this a bit more  
//    this.saveToDB(callback);
}

/** Imports the gene name section of the file */
LearningImporter.prototype.importGeneNames = function(gene_line, learning_type) {
    if (gene_line.indexOf('Patient\tType') == -1 && gene_line.indexOf('Patient\tDiagnosis') == -1) {
        console.error('Malformed file');
    }
    
    gene_line      = gene_line.replace('Patient\tType\t', '').replace('Patient\tDiagnosis\t', ''); // strip off first suffix
    
    this.genes     = [];
    var gene_names = gene_line.split('\t');
    
    var _this      = this;
    // create new genes
    gene_names.forEach(function(gene_name, index, arry) {
        _this.gene_set.addGene(new Gene(gene_name, learning_type));
    }, this);
}

/** Imports a patient line of the file */
LearningImporter.prototype.importPatient = function(patient_line, learning_type) {
    // skip it if no information (not an error, just extra blank line)
    if (patient_line == '') {
        return;
    }
    // TODO: validate that the line properly starts
    var gene_expressions = patient_line.split('\t');
    
    // get the meta information at the front
    var patientIndex    = parseInt(gene_expressions[0]);
    var patientDisease  = gene_expressions[1];
    
    var patient = new Patient(patientIndex, patientDisease, learning_type);
    
    // add the patient to our own internal list
    LearningImporter.prototype.patient_set.addPatient(patient);
    
    gene_expressions = gene_expressions.slice(2); // ignore first two items since just the disease type
    
    var _this = this;
    gene_expressions.forEach(function(expression, index, arry) {
        // match up with already imported genes
        // I wish there was a zip method!
        // tell the genes about their expression level in this sample (patient) 
        _this.gene_set.getGene(index).setExpressionLevel(patient, parseFloat(expression));
        
        // but also add this gene to ourselves (we need to know about our own set of expression levels)
        patient.addGene(_this.gene_set.getGene(index));
    }, this);
}


/**
* Imports the given local file into the database.
* Spawns off a worker thread and keeps us posted via callbacks
* the call back should accept a percent done out of 100
*/
LearningImporter.prototype.import = function(file, learning_type, callback) {
    var reader = new FileReader();
    var _this = this;
    
    reader.onerror = function() {
        callback(-1);
    }
    reader.onabort = function() {
        callback(-1);
    }
    reader.onloadstart = function() {
        callback(0);
    }
    reader.onprogress = function(evt) {
        if (! evt.lengthComputable) {
            return;
        }
        
        var percentLoaded = Math.floor((evt.loaded / evt.total) * 100);
        
        // we don't want to call the callback twice with 100
        if (percentLoaded < 100) {
            callback(percentLoaded);
        }
        
    }
    reader.onload = function(e) {

        _this.raw_contents = e.target.result;
        
        // simple benchmark
        var start = new Date().getTime();
        
        // now do the really heavy lifting
        // TODO: move the callback until after the network nonsense
        _this.importFile(learning_type, function(percent) {
            callback(100);
        });
        
        console.log('Took ' + (new Date().getTime() - start) / 1000 + 's');
    }
    
    reader.readAsText(file);
    
}