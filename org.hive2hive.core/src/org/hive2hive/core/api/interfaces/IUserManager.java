package org.hive2hive.core.api.interfaces;

import java.nio.file.Path;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.processes.framework.interfaces.IProcessComponent;
import org.hive2hive.core.security.UserCredentials;

/**
 * Basic interface for all user operations.
 * 
 * @author Christian, Nico, Seppi
 * 
 */
public interface IUserManager extends IManager {

	/**
	 * Each user must be registered in the Hive2Hive network. This call creates a new UserProfile for the
	 * given user credentials.
	 * 
	 * @param credentials the user credentials. Note that the user id must be unique, the password and pin
	 *            must be kept private to ensure the security.
	 * @return an observable process component
	 * @throws NoPeerConnectionException if the peer is not connected to the network
	 */
	IProcessComponent register(UserCredentials credentials) throws NoPeerConnectionException;

	/**
	 * Login a (registered) user with the same credentials as {@link IUserManager#register(UserCredentials)}
	 * has been called. After login, the root folder gets synchronized.
	 * 
	 * @param credentials the user credentials
	 * @param rootPath the path of the Hive2Hive root folder. In this folder, all files are synchronized.
	 * @return an observable process component
	 * @throws NoPeerConnectionException if the peer is not connected to the network
	 */
	// TODO the file root path should not be part of this interface, but have a place in IFileManagement
	IProcessComponent login(UserCredentials credentials, Path rootPath) throws NoPeerConnectionException;

	/**
	 * When a user is done, he should logout himself, killing the session at the current node. After logout,
	 * he does not receive any messages / notifications anymore and files don't get synchronized anymore.
	 * 
	 * @return an observable process component
	 * @throws NoPeerConnectionException if the peer is not connected to the network
	 * @throws NoSessionException no user has logged in
	 */
	IProcessComponent logout() throws NoPeerConnectionException, NoSessionException;
}