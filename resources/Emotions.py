import sys
import json
import numpy as np
import tensorflow.lite as tflite
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
import os


script_dir = os.path.dirname(os.path.abspath(__file__))
tokenizer_path = os.path.join(script_dir, "tokenizer.json")

with open(tokenizer_path, "r", encoding="utf-8") as f:
    tokenizer_data = json.load(f)

tokenizer = Tokenizer()
tokenizer.word_index = tokenizer_data

model_path = os.path.join(script_dir, "emotion_model.tflite")
if not os.path.exists(model_path):
    raise FileNotFoundError(f"El archivo del modelo no existe en: {model_path}")

interpreter = tflite.Interpreter(model_path=model_path)
interpreter.allocate_tensors()

input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()
MAX_LENGTH = input_details[0]['shape'][1]  

emotions = ["ira", "amor", "alegría", "miedo", "tristeza"]

def predict_emotion(text):
    input_sequence = tokenizer.texts_to_sequences([text])
    padded_input_sequence = pad_sequences(input_sequence, maxlen=MAX_LENGTH)
    input_data = np.array(padded_input_sequence, dtype=np.float32)
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()
    prediction = interpreter.get_tensor(output_details[0]['index'])[0]
    emotion_probs = {emotions[i]: round(float(prediction[i] * 100)) for i in range(len(emotions))}
    return emotion_probs

def save_results(results, append_mode=False):
    resources_dir = os.path.join(os.path.dirname(__file__), "..", "resources")
    os.makedirs(resources_dir, exist_ok=True)  # Crea la carpeta si no existe
    file_path = os.path.join(resources_dir, "EmotionsResume.txt")

    mode = "a" if append_mode else "w"
    with open(file_path, mode, encoding="utf-8") as f:
        for result in results:
            f.write(", ".join([f"{key}, {value}" for key, value in result.items()]) + "\n")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Uso: python Emotions.py <texto_o_archivo> <modo>")
        sys.exit(1)
    
    input_source = sys.argv[1]
    mode = sys.argv[2]  # "text" para un solo texto, "file" para un archivo
    
    results = []
    
    if mode == "file":
        try:
            with open(input_source, "r", encoding="utf-8") as file:
                for line in file:
                    result = predict_emotion(line.strip())
                    results.append(result)
        except FileNotFoundError:
            print(f"El archivo {input_source} no se encontró.")
            sys.exit(1)
    else:
        result = predict_emotion(input_source)
        results.append(result)
    
    save_results(results)