/** Simple container for keeping a set of genes*/
function GeneSet(learning_type) {
    this.learning_type = learning_type;
    this.type   = 'GeneSet';
    this.genes  = [];
}

GeneSet.prototype.addGene = function(gene) {
    gene.learning_type = this.learning_type;
    this.genes.push(gene);
}

GeneSet.prototype.getGene = function(index) {
    return this.genes[index];
}