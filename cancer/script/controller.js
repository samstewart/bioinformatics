/**
* We explicitly refactor all of the UI code into a controller class
* for interacting with the database.
*/
$(document).ready(function() {
    setupBindings();
});

function setupBindings() {
    
    // tell jquery to include the drag/drop properties
    jQuery.event.props.push('dataTransfer');
    
    var trainingImporter = new LearningImporter();
    var testingPredictor = new Predictor();
            
    // add the event handler to handle a file drag and drop upload
    $('#trainingFileDrop').on('drop', function(evt) {
        // stop the default event from propegating
        evt.preventDefault();
        evt.stopPropagation();
        
        // simply output the files
        var output = [];
        var files = evt.dataTransfer.files;
        
        var f = files[0];
        
        trainingImporter.import(f, 'training', function(progress) {
           if (progress < 0) {
               console.error('Error loading file: ' + f.name);
               return;
           }
           
           $('#trainingProgressBar').attr('value', progress);
        });
        
        
    });
    
    // prevent the events from responding normally when the user drops the file
    $('#trainingFileDrop').on('dragover', function(evt) {
       evt.preventDefault();
       evt.stopPropagation();
       evt.dataTransfer.dropEffect = 'copy'; 
    });
    
    // add the event handler to handle a file drag and drop upload for the testing data
    $('#testingFileDrop').on('drop', function(evt) {
        // stop the default event from propegating
        evt.preventDefault();
        evt.stopPropagation();
        
        // simply output the files
        var output = [];
        var files = evt.dataTransfer.files;
        var f = files[0];
        
        
        // import the testing patients
        testingPredictor.import(f, 'testing', function(progress) {
           if (progress < 0) {
               console.error('Error loading file: ' + f.name);
               return;
           }
           
           $('#testingProgressBar').attr('value', progress); 

        });
        
        
    });

    
    $('#makePredictions').click(function(evt) {
        evt.preventDefault();
        evt.stopPropagation();
        
        // must be called *after* the main data has been imported for training
        testingPredictor.loadDataFromTraining(trainingImporter);
        
        // now we list the most preditive genes
        var output = testingPredictor.predictive_genes.map(function(gene, index, arry) {
            return '(' + gene.name + '): ' + gene.getClassDistinctionCorrelation(testingPredictor.possible_diseases[0], testingPredictor.possible_diseases[1]);
        }).join('\n');
        
        $('#predictiveGeneArea').val(output);
        
        // once the data is loaded, we test this thing
        var results = testingPredictor.testPredictor();

        $('#accuracyTesting').val(results['accuracy']);

        var output = results['predictions'].map(function(prediction, index, arry) {
            return prediction.getFancyString();
        }).join('\n');

        $('#classificationArea').val(output);
        
        var visualizer = new Visualization();

        visualizer.visualize(testingPredictor, results['predictions'], $('#visualCanvas')[0]);
        
    })
    
    // prevent the events from responding normally when the user drops the file 
    // containing testing data
    $('#testingFileDrop').on('dragover', function(evt) {
       evt.preventDefault();
       evt.stopPropagation();
       evt.dataTransfer.dropEffect = 'copy'; 
    });
}