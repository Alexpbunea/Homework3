import json
import os

def remove_page_number_and_delete_malformed_from_json(json_dir):
    count = 0
    
    # Iterate through all files in the specified directory
    for filename in os.listdir(json_dir):
        if filename.endswith(".json"):
            file_path = os.path.join(json_dir, filename)
            
            try:
                with open(file_path, "r", encoding="utf-8") as file:
                    data = json.load(file)
                updated = False
                # Remove the 'PAPER'S NUMBER OF TABLES' key if it exists
                if "PAPER'S NUMBER OF TABLES" in data:
                    del data["PAPER'S NUMBER OF TABLES"]
                    updated = True
                if "global_footnotes" in data:
                    del data["global_footnotes"]
                    updated = True
                if "INFO" in data:
                    print(f"Deleting the file {filename}")
                    os.remove(file_path)
                    count += 1
                    continue
                
                # Save the modified JSON file
                if updated:
                    with open(file_path, "w", encoding="utf-8") as file:
                        json.dump(data, file, ensure_ascii=False, indent=4)
                    print(f"File {filename} successfully updated.")
            
            except json.JSONDecodeError as e:
                print(f"The file {filename} is malformed and will be deleted.")
                print(e)
                os.remove(file_path)
                count += 1
            except UnicodeDecodeError as e:
                print(f"The file {filename} is malformed and will be deleted.")
                print(e)
                os.remove(file_path)
                count += 1
        else:
            print(f"The file {filename} is not a JSON file, skipping.")
    print(f"\nDeleted {count} files")
# Path to the directory containing the JSON files
json_directory = r"../all_tables"

# Call the function
remove_page_number_and_delete_malformed_from_json(json_directory)