import os
import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt

def generate_surefire_method_results_graph_per_file(reports_dir="target/surefire-reports"):
    for filename in os.listdir(reports_dir):
        if filename.endswith(".xml"):
            filepath = os.path.join(reports_dir, filename)
            tree = ET.parse(filepath)
            root = tree.getroot()

            pass_methods = []
            fail_methods = []
            failure_messages = {}

            for testcase in root.findall("testcase"):
                method_name = testcase.get("name")
                failure = testcase.find("failure")
                if failure is not None:
                    fail_methods.append(method_name)
                    failure_messages[method_name] = failure.text.strip() if failure.text else "Failure without message"
                else:
                    pass_methods.append(method_name)

            all_methods = pass_methods + fail_methods
            y_pos = range(len(all_methods))

            plt.figure(figsize=(10, len(all_methods) * 0.5))

            colors = ['green'] * len(pass_methods) + ['red'] * len(fail_methods)
            plt.barh(y_pos, [1] * len(all_methods), color=colors, alpha=0.7)

            plt.yticks(y_pos, all_methods)
            plt.title(f"Test Method Results - {filename}")

            plt.xlim(0, 2)
            plt.gca().get_xaxis().set_visible(False)

            plt.tight_layout()
            output_file = f"{os.path.splitext(filename)[0]}_results.png"
            plt.savefig(output_file)
            print(f"Graph saved to {output_file}")
            plt.close() # Close the figure to free up memory for the next file

if __name__ == "__main__":
    generate_surefire_method_results_graph_per_file()
