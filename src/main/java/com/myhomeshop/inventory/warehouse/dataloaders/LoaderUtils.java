package com.myhomeshop.inventory.warehouse.dataloaders;

/**
 * As the Loaders become more robust and resilient to data inconsistency utils for validation
 * and component options adding will grow
 */
public class LoaderUtils {
    /**
     * Creates File2 component Endpoint URI from given inputs
     * @param filePath path to poll for file
     * @param fileName name of file to pick from polling path
     * @return valid File2 camel component URI
     */
    public static String getFileEndPointFromPath(String filePath, String fileName) {
        StringBuilder sb = new StringBuilder("file://");
        sb.append(filePath);
        if (fileName != null && !fileName.equals("")) {
            sb.append("?fileName=").append(fileName);
        }
        return sb.toString();
    }
}
