import string
import collections
import numpy as np
import nltk
from nltk import word_tokenize
from nltk.stem import PorterStemmer
from nltk.corpus import stopwords
from sklearn.cluster import KMeans
from sklearn.feature_extraction.text import TfidfVectorizer
from pprint import pprint

def process_text(text, stem=False):
    """ Tokenize text and stem words removing punctuation """
    # text = text.translate(None, string.punctuation)
    tokens = word_tokenize(text)
    if stem:
        stemmer = PorterStemmer()
        tokens = [stemmer.stem(t) for t in tokens]
    return tokens


def cluster_texts(texts, clusters=2):
    """ Transform texts to Tf-Idf coordinates and cluster texts using K-Means """
    vectorizer = TfidfVectorizer(tokenizer=process_text,
                                 stop_words=stopwords.words('english'),
                                 max_df=0.5,
                                 min_df=0.1,
                                 lowercase=True)
    tfidf_model = vectorizer.fit_transform(texts)
    km_model = KMeans(n_clusters=clusters)
    km_model.fit(tfidf_model)
    return km_model
    # clustering = collections.defaultdict(list)
    # for idx, label in enumerate(km_model.labels_):
        # clustering[label].append(idx)
    # return clustering

def display_output(km_model):
    with open('output.txt', 'w') as f:
        for idx, label in enumerate(km_model.labels_):
            line = '{} {}\n'.format(idx, label)
            print(line)
            f.write(line)

def get_input():
    input = []
    with open('prob.txt') as f:
        data = f.readlines()
    for line in data:
        try:
            sentence = line.strip().split(maxsplit=1)[1:][0]
        except IndexError:
            sentence = ''
        input.append(sentence)
    return input


if __name__ == "__main__":
    # nltk.data.path = ['/Users/Snm/Downloads/nltk_data']
    nltk.data.path = ['C:\\Users\\Snm\\AppData\\Roaming\\nltk_data']
    # nltk.download()
    articles = get_input()
    # clusters = cluster_texts(articles[:100], 5)
    # pprint(dict(clusters))
    trained_model = cluster_texts(articles, 25)
    display_output(trained_model)
