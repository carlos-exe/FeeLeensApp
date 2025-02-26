import nltk
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import sys
import os


lemmatizer = WordNetLemmatizer()
stop_words = set(stopwords.words('english'))

def extract_words(document):
    tokens = nltk.word_tokenize(document.lower())
    return set(
        lemmatizer.lemmatize(word) for word in tokens
        if word.isalpha() and word not in stop_words
    )

def load_data():
    result = []
    for filename in ["positives.txt", "negatives.txt"]:
        with open(filename, encoding="utf-8") as f:
            result.append([extract_words(line) for line in f.read().splitlines()])
    return result

def get_unique_words(positives, negatives):
    words = set()
    for document in positives + negatives:
        words.update(document)
    return words

def generate_features(documents, words, label):
    features = []
    for document in documents:
        features.append(({
            word: (word in document) for word in words
        }, label))
    return features

def classify_text(classifier, text, words):
    document_words = extract_words(text)
    features = {word: (word in document_words) for word in words}
    prob_dist = classifier.prob_classify(features)
    return {label: round(prob_dist.prob(label) * 100) for label in prob_dist.samples()}

def save_results(positives, negatives, append_mode=False):
    resources_dir = os.path.join(os.path.dirname(__file__), "..", "resources")
    os.makedirs(resources_dir, exist_ok=True)  # Crea la carpeta si no existe
    file_path = os.path.join(resources_dir, "PNResume.txt")

    mode = "a" if append_mode else "w"
    with open(file_path, mode, encoding="utf-8") as f:
        f.write(f"Positives, {positives}, Negatives, {negatives}\n")

def main():
    if len(sys.argv) < 3:
        print("Uso: python PositivesAndNegatives.py <texto_o_archivo> <modo>")
        sys.exit(1)
    
    input_source = sys.argv[1]
    mode = sys.argv[2]  # "text" para un solo texto, "file" para un archivo
    
    positives, negatives = load_data()
    words = get_unique_words(positives, negatives)
    training = []
    training.extend(generate_features(positives, words, "Positive"))
    training.extend(generate_features(negatives, words, "Negative"))
    classifier = nltk.NaiveBayesClassifier.train(training)
    
    if mode == "file":
        pos_count = 0
        neg_count = 0
        try:
            with open(input_source, "r", encoding="utf-8") as file:
                for line in file:
                    result = classify_text(classifier, line.strip(), words)
                    if result.get("Positive", 0) > result.get("Negative", 0):
                        pos_count += 1
                    else:
                        neg_count += 1
        except FileNotFoundError:
            print(f"El archivo {input_source} no se encontró.")
            sys.exit(1)
        save_results(pos_count, neg_count)
    else:
        result = classify_text(classifier, input_source, words)
        save_results(result.get("Positive", 0), result.get("Negative", 0))

if __name__ == "__main__":
    main()