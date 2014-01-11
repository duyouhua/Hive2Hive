package org.hive2hive.core.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.HashSet;
import java.util.Set;

import org.hive2hive.core.file.FileManager;

/**
 * Tree implementation for the file tree. It stores the keys for the files and it's logic location.
 * 
 * @author Nico
 * 
 */
public class FileTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private final KeyPair keyPair;
	private final boolean isFolder;
	private FileTreeNode parent;
	private String name;
	private byte[] md5LatestVersion;
	private KeyPair protectionKeys = null;
	private final Set<FileTreeNode> children;

	/**
	 * Constructor for child nodes of type 'folder'
	 * 
	 * @param parent
	 * @param keyPair
	 * @param name
	 * @param isFolder
	 */
	public FileTreeNode(FileTreeNode parent, KeyPair keyPair, String name) {
		this(parent, keyPair, name, true, null);
	}

	/**
	 * Constructor for child nodes of type 'file'
	 * 
	 * @param parent
	 * @param keyPair
	 * @param name
	 * @param isFolder
	 */
	public FileTreeNode(FileTreeNode parent, KeyPair keyPair, String name, byte[] md5LatestVersion) {
		this(parent, keyPair, name, false, md5LatestVersion);
	}

	private FileTreeNode(FileTreeNode parent, KeyPair keyPair, String name, boolean isFolder,
			byte[] md5LatestVersion) {
		this.parent = parent;
		this.protectionKeys = parent.getProtectionKeys();
		this.keyPair = keyPair;
		this.name = name;
		this.isFolder = isFolder;
		this.setMD5(md5LatestVersion);
		parent.addChild(this);
		children = new HashSet<FileTreeNode>();
	}

	/**
	 * Constructor for root node
	 * 
	 * @param keyPair
	 */
	public FileTreeNode(KeyPair keyPair, KeyPair domainKey) {
		this.keyPair = keyPair;
		this.protectionKeys = domainKey;
		this.isFolder = true;
		this.parent = null;
		children = new HashSet<FileTreeNode>();
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileTreeNode getParent() {
		return parent;
	}

	public void setParent(FileTreeNode parent) {
		this.parent = parent;
	}

	public Set<FileTreeNode> getChildren() {
		return children;
	}

	public void addChild(FileTreeNode child) {
		// only add once
		if (getChildByName(child.getName()) == null)
			children.add(child);
	}

	public void removeChild(FileTreeNode child) {
		if (!children.remove(child)) {
			// remove by name
			children.remove(getChildByName(child.getName()));
		}
	}

	/**
	 * Finds a child with a name. If the child does not exist, null is returned
	 * 
	 * @param name
	 * @return
	 */
	public FileTreeNode getChildByName(String name) {
		if (name != null) {
			String withoutSeparator = name.replaceAll(FileManager.getFileSep(), "");
			for (FileTreeNode child : children) {
				if (child.getName().equalsIgnoreCase(withoutSeparator)) {
					return child;
				}
			}
		}
		return null;
	}

	public KeyPair getProtectionKeys() {
		if (protectionKeys == null) {
			return parent.getProtectionKeys();
		} else {
			return protectionKeys;
		}
	}

	public void setProtectionKeys(KeyPair protectionKeys) {
		if (isRoot())
			throw new IllegalStateException("Not allowed to change root's protection key.");
		this.protectionKeys = protectionKeys;
	}

	public byte[] getMD5() {
		return md5LatestVersion;
	}

	public void setMD5(byte[] md5LatestVersion) {
		this.md5LatestVersion = md5LatestVersion;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public boolean isShared() {
		// TODO implement is shared
		return false;
	}

	public boolean hasShared() {
		// TODO implement has shared
		return false;
	}

	public boolean canWrite() {
		if (protectionKeys == null) {
			return parent.canWrite();
		} else {
			return true;
		}
	}

	/**
	 * Returns the full path (starting at the root) of this node
	 * 
	 * @return
	 */
	public Path getFullPath() {
		if (parent == null) {
			return Paths.get("");
		} else {
			return Paths.get(parent.getFullPath().toString(), getName());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FileTreeNode [");
		sb.append("name=").append(name);
		sb.append(" path=").append(getFullPath());
		sb.append(" isFolder=").append(isFolder);
		sb.append(" children=").append(children.size()).append("]");
		return sb.toString();
	}
}
