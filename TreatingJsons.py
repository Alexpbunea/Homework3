import os
import json

def remove_field(data, field, file_path):
    try:
        if field in data:
            if field == "INFO":
                os.remove(file_path)
                return [field, 0]  #eliminating the field
            else:
                del data[field]
                return [field, 1]  #eliminated the corresponding field
        else:
            return [field, -1]  #Not found field
    except Exception as e:
        raise Exception(f"Error removing the field '{field}' in file '{file_path}': {e}")

def remove_brackets(data, field):
    if field in data:
        try:
            if isinstance(data[field], list):
                if isinstance(data[field], list) and all(isinstance(i, list) for i in data[field]):
                    stringWithAllCells = ""
                    for row in data[field]:
                        for cell in row:
                            cell_cleaned = " ".join(cell.split())
                            stringWithAllCells += cell_cleaned
                    data[field] = stringWithAllCells
                else:
                    data[field] = " ".join(data[field])



        except Exception as e:
            print(f"Error processing brackets in field '{field}': {e}") #only a print because [] is not compulsary


def process_json_file(file_path):
    with open(file_path, "r", encoding="utf-8") as file:
        data = json.load(file)

    fields_to_remove = ["INFO", "PAPER'S NUMBER OF TABLES", "global_footnotes"]

    for field in fields_to_remove:
        result = remove_field(data, field, file_path)
        if result[1] == 0:  # Archivo eliminado
            print(f"File {file_path} deleted due to field '{field}'.")
            return
        elif result[1] == 1:
            print(f"Field '{field}' removed from file {file_path}.")
        elif result[1] == -1:
            print(f"Field '{field}' not found in file {file_path}.")



    fields_to_clean = ["caption", "table", "footnotes", "references"]
    for table_id, table_data in data.items():
        for field in fields_to_clean:
            remove_brackets(table_data, field)


    with open(file_path, "w", encoding="utf-8") as file:
        json.dump(data, file, ensure_ascii=False, indent=4)
    print(f"File {file_path} updated successfully.")


def remove_page_number_from_json(json_dir):
    for filename in os.listdir(json_dir):
        if filename.endswith(".json"):
            file_path = os.path.join(json_dir, filename)
            try:
                process_json_file(file_path)
            except Exception as e:
                print(f"Error processing file '{file_path}': {e}")
        else:
            print(f"Skipping non-JSON file: {filename}")



directorio_json = r"C:\Users\Dell XPS 9510\Desktop\java\t2\jsons"

print()
print()

remove_page_number_from_json(directorio_json)

print()
print()