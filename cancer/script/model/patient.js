/**
* Simple object representing a patient
* Patients are equivalent to samples. Each patient has a set of genes.
*/

function Patient(p_index, disease, learning_type) {
    this.type           = 'Patient';
    this.p_index        = p_index;
    this.disease        = disease;
    this.learning_type  = learning_type;
    this.genes          = {};
    this.gene_expression_levels = {}; // we use this later but don't need full gene info
}

Patient.prototype.addGene = function(gene) {
    this.genes[gene.name] = gene;
    this.gene_expression_levels[gene.name] = gene.getExpressionLevel(this);
}

Patient.prototype.getGeneExpressionByName = function(name) {
    return this.gene_expression_levels[name];
}
Patient.prototype.getGeneByName = function(name) {
    return this.genes[name];
}