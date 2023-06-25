const natural = require('natural');
const wordsToNumbers = require('words-to-numbers');

exports.get_query_dict = (text) => {
    // parse text
    var modifiedText = text.replace(/\b(a|one|two|three|four|five|six|seven|eight|nine) (hundred|thousand|million|billion)\b/gi, (match) => {
        return wordsToNumbers.wordsToNumbers(match);
      });
    modifiedText = modifiedText.replace(/\b(a|an)\b/gi, "one");

    const tokenizer = new natural.WordTokenizer();
    const tokens = tokenizer.tokenize(modifiedText);

    var ruleSet = new natural.RuleSet('EN');
    var lexicon = new natural.Lexicon('EN', 'CD');

    const nounInflector = new natural.NounInflector();
    const posTagger = new natural.BrillPOSTagger(lexicon, ruleSet);

    //posTagger.load();
    nounTags = ['NN', 'NNS', 'NNP', 'NNPS']

    const taggedTokens = posTagger.tag(tokens);
    console.log(taggedTokens.taggedWords);
    let quantity, item;

    for (let taggedToken of taggedTokens.taggedWords) {
        if (taggedToken.tag == 'CD') {
            quantity = parseInt(taggedToken.token);
            if (isNaN(quantity)) {
                quantity = wordsToNumbers.wordsToNumbers(taggedToken.token)
                console.log(quantity)
            }
        } else if (nounTags.includes(taggedToken.tag)) {
            item = nounInflector.singularize(taggedToken.token);
        }
    }
    dict = {'item':item, 'quantity': quantity}
    return dict
};
