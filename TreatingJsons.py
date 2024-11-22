import os
import json

def remove_page_number_from_json(json_dir):
    # Iterar por todos los archivos en el directorio especificado
    for filename in os.listdir(json_dir):
        if filename.endswith(".json"):
            file_path = os.path.join(json_dir, filename)
            
            # Abrir y leer el archivo JSON
            with open(file_path, "r", encoding="utf-8") as file:
                data = json.load(file)
                
            # Eliminar la clave 'page number' si existe
            if "PAPER'S NUMBER OF TABLES" in data:
                del data["PAPER'S NUMBER OF TABLES"]
            elif "global_footnotes" in data:
                del data["global_footnotes"]
            elif len(data) == 1 and "INFO" in data:
                print(f"Eliminando el archivo {filename}")
                os.remove(file_path)
                continue
            # Guardar el archivo JSON modificado
            with open(file_path, "w", encoding="utf-8") as file:
                json.dump(data, file, ensure_ascii=False, indent=4)

            print(f"Archivo {filename} actualizado exitosamente.")
        else:
            print(f"El archivo {filename} no es un archivo JSON, se omite.")

# Ruta al directorio que contiene los archivos JSON
directorio_json = r"C:\Users\Dell XPS 9510\Desktop\java\t2\jsons"

# Llamar a la función
remove_page_number_from_json(directorio_json)
