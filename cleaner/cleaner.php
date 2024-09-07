<?php
	// Define source directory and destination directory
	$sourceDir = 'C:/Users/Administrator/IdeaProjects/ECM4/src/main/webapp/';
	$destinationDir = 'C:/Users/Administrator/Documents/bg/cleaner/holding';

	// Define the path to the text file that contains the list of allowed files
	$allowedFilesTxt = 'C:/Users/Administrator/Documents/bg/cleaner/allowed_files.txt';

	// Create the destination directory if it doesn't exist
	if (!is_dir($destinationDir)) {
		mkdir($destinationDir, 0777, true);
	}

	// Initialize an array to store allowed files as a set for fast lookup
	$allowedFilesSet = [];

	// Read the allowed_files.txt and build the allowed_files set
	$allowedFileHandle = fopen($allowedFilesTxt, 'r');
	if ($allowedFileHandle) {
		while (($line = fgets($allowedFileHandle)) !== false) {
			$filePath = rtrim($sourceDir . trim($line));
			$cleanedFilePath = strtolower(str_replace(' ', '', $filePath));
			$allowedFilesSet[$cleanedFilePath] = true;
		}
		fclose($allowedFileHandle);
	}

	// Output the allowed files for debugging (optional)
	// echo "Allowed files:\n";
	// foreach (array_keys($allowedFilesSet) as $file) {
	//     echo $file . "\n";
	// }
	// echo "\n";

	// Function to recursively find all .js and .css files in a directory
	function findFiles($dir, $extensions) {
		$files = [];
		$iterator = new RecursiveIteratorIterator(new RecursiveDirectoryIterator($dir));
		foreach ($iterator as $file) {
			if ($file->isFile()) {
				$extension = strtolower(pathinfo($file->getFilename(), PATHINFO_EXTENSION));
				if (in_array($extension, $extensions)) {
					$files[] = $file->getPathname();
				}
			}
		}
		return $files;
	}

	// Find all .js and .css files in sourceDir recursively
	//$files = findFiles($sourceDir, ['js', 'css']);
	$files = findFiles($sourceDir, ['js']);

	// Process files and move if not allowed
	foreach ($files as $file) {
		$cleanedFilePath = strtolower(str_replace(' ', '', $file));
		if (!isset($allowedFilesSet[$cleanedFilePath])) {
			// Recreate the directory structure in the destination directory
			$relativePath = substr($file, strlen($sourceDir));
			$targetDir = $destinationDir . DIRECTORY_SEPARATOR . dirname($relativePath);

			// Create the target directory if it doesn't exist
			if (!is_dir($targetDir)) {
				mkdir($targetDir, 0777, true);
			}

			// Move the file to the recreated directory structure in destination
			echo "Moving: $file to $targetDir\n";
			rename($file, $targetDir . DIRECTORY_SEPARATOR . basename($file));
		} else {
			// Optional: Uncomment the line below if you want to debug which files are kept
			// echo "Keeping: $file\n";
		}
	}

	echo "Operation completed.\n";
	exit;	
?>